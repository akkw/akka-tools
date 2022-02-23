/*
 * Copyright © 2022 akka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akka.tools.bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleEventBus<T> implements Bus<T> {

    private static final Logger logger = LoggerFactory.getLogger(SingleEventBus.class);


    private static final AtomicInteger numberPlate = new AtomicInteger(0);

    /**
     * 公交成名.
     */
    private final String busName;

    /**
     * 负责运送事件.
     */
    private final Thread bus;

    /**
     * 事件目的地.
     */
    private final List<Station<T>> stations;

    /**
     * 事件.
     */
    private final BlockingQueue<Event<T>> events;

    /**
     * 发车标记.
     */
    private volatile boolean isRun;

    /**
     * 优雅关闭.
     */
    private final boolean gracefulClose;

    /**
     * 等待下车的Event.
     */
    private final ThreadPoolExecutor reachGoal;

    public SingleEventBus() {
        this(String.format("Default-SingleEventBus %d", numberPlate.getAndIncrement()), false);
    }

    public SingleEventBus(String name) {
        this(name, false);
    }

    public SingleEventBus(boolean gracefulClose) {
        this(String.format("SingleEventBus %d", numberPlate.getAndIncrement()), gracefulClose);
    }

    public SingleEventBus(String name, boolean gracefulClose) {
        this.busName = name;
        this.bus = new Thread(new Driver(), this.busName);
        this.events = new LinkedBlockingDeque<>();
        this.stations = new CopyOnWriteArrayList<>();
        this.gracefulClose = gracefulClose;
        this.reachGoal = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    public void start() {
        bus.start();
        isRun = true;
        logger.info("{} start success", busName);
    }

    @Override
    public void stop() {
        isRun = false;
        if (!gracefulClose) {
            reachGoal.shutdownNow();
        }
    }

    @Override
    public void addEvent(Event<T> event) {
        if (!isRun) {
            throw new RuntimeException(String.format("bus %s already shutdown", busName));
        }
        this.events.add(event);
    }

    @Override
    public void addStation(Station<T> station) {
        stations.add(station);
    }

    class Driver implements Runnable {

        @Override
        public void run() {
            while (isRun || gracefulClose) {
                // 停止运行
                // 不是优雅关闭
                Event<T> event;
                try {
                    event = events.poll(10, TimeUnit.MILLISECONDS);

                    if (event != null) {
                        reachGoal.execute(new EventExecutor<>(event, stations));
                    }

                    // 关闭
                    if (!isRun) {
                        reachGoal.shutdown();
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
