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

public class SingleEventBus<T> extends AbstractEventBus<T> {

    private static final Logger logger = LoggerFactory.getLogger(SingleEventBus.class);


    private static final AtomicInteger numberPlate = new AtomicInteger(0);


    /**
     * 等待下车的Event.
     */
    private final ThreadPoolExecutor reachGoal;

    private final Driver driver = new Driver();

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
        super(name, gracefulClose);
        super.bus = new Thread(new Driver(), name);
        this.reachGoal = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }

    @Override
    public void start() {
        super.start();
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
