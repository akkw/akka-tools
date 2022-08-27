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

import org.junit.Before;
import org.junit.Test;


import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * SingleEventBus 测试用例.
 */
public class SingleEventBusTest {
    AsyncEventBus<String> bus;
    volatile boolean running = false;
    @Before
    public void before() {

        bus = new AsyncEventBus<>();

        bus.start();
        running = true;
        bus.addStation(new Station1());
        bus.addStation(new Station1());
        bus.addStation(new Station2());
        bus.addStation(new Station2());
    }

    @Test
    public void test() throws InterruptedException {
        ArrayList<Station<String>> stations = new ArrayList<>();
        stations.add(new Station3());
        stations.add(new Station4());
        final int[] count = {0};
        new Thread(()-> {
            while (running){
                try {
                    if (count[0] %10 ==0) {
                        bus.addEvent(new Event<>("test", stations));
                    }
                    count[0]++;
                    bus.addEvent(new Event<>("test"));
                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        TimeUnit.SECONDS.sleep(10);
        bus.shutdown();
        running = false;
        System.out.println("shutdown");
        do {

        } while (bus.eventSize() != 0);
    }

    static class Station1 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) throws InterruptedException {
            System.out.printf("Station1: %s%n", event.getLoad());
        }
    }

    static class Station2 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) {
            System.out.printf("Station2: %s%n", event.getLoad());
        }
    }

    static class Station3 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) throws InterruptedException {
            System.out.printf("Station3: %s%n", event.getLoad());
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }

    static class Station4 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) {
            System.out.printf("Station4: %s%n", event.getLoad());
        }
    }
}
