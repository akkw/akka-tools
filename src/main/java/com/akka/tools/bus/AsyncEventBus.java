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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncEventBus<T> extends AbstractEventBus<T> {

    private static final Logger logger = LoggerFactory.getLogger(AsyncEventBus.class);


    private static final AtomicInteger numberPlate = new AtomicInteger(0);


    private final ThreadPoolExecutor eventExecutor;


    private final ThreadPoolExecutor commonExecutor;


    private volatile long heartbeatInterval;


    private volatile BusHeartbeat heartbeat;

    private int idleTime;

    private BusIdle idle;

    public AsyncEventBus() {
        this(String.format("Default-SingleEventBus %d", numberPlate.getAndIncrement()),
                new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>()));
    }

    public AsyncEventBus(String name, ThreadPoolExecutor executor) {
        super(name);
        super.bus = new Thread(new Driver(), name);
        this.eventExecutor = executor;
        this.commonExecutor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    }


    private class Driver implements Runnable {

        private long prevTimestamp;

        private int idleTime;

        @Override
        public void run() {
            while (isRunning() || isShutdown()) {
                // 停止运行
                // 不是优雅关闭
                Event<T> event;
                try {
                    event = events.poll(10, TimeUnit.MILLISECONDS);

                    if (isShutdownNow()) {
                        break;
                    }

                    if (event != null) {



                        eventExecutor.execute(new EventExecutor<>(event, stations));
                        idleTime = 0;
                    } else {
                        idleTime++;
                    }

                    if (heartbeat != null) {
                        long time = System.currentTimeMillis();
                        if (time - prevTimestamp > heartbeatInterval) {

                            commonExecutor.execute(new HeartbeatExecutor(heartbeat));
                            prevTimestamp = time;
                        }
                    }
                    // TODO 保证心跳
                    if (idle != null && AsyncEventBus.this.idleTime > this.idleTime) {
                        idle.odIdle();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            clear();
        }
    }

    private void clear() {
        if (isShutdownNow()) {
            shutdownNowClear();
        } else if (isShutdown()){
            shutdownClear();
        }
    }

    private void shutdownClear() {
        eventExecutor.shutdown();
        commonExecutor.shutdown();
    }

    private void shutdownNowClear() {
        events.clear();
        stations.clear();
        eventExecutor.shutdownNow();
        commonExecutor.shutdownNow();
    }

    public void heartbeat(BusHeartbeat heartbeat) {
        this.heartbeat = heartbeat;
    }

    public void heartbeatInterval(long heartbeat) {
        this.heartbeatInterval = heartbeat;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    public void setIdle(BusIdle idle) {
        this.idle = idle;
    }

    public int eventSize() {
        return events.size() + eventExecutor.getQueue().size();
    }
}
