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

import com.akka.tools.bus.Event;
import com.akka.tools.bus.SingleEventBus;
import com.akka.tools.bus.Station;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * SingleEventBus 测试用例.
 */
public class SingleEventBusTest {
    SingleEventBus<String> bus;

    @Before
    public void before() {

        bus = new SingleEventBus<>();

        bus.addStation(new Station1());
        bus.addStation(new Station1());
        bus.addStation(new Station2());
        bus.addStation(new Station2());

        bus.start();

    }

    @Test
    public void test() throws InterruptedException {
        ArrayList<Station<String>> stations = new ArrayList<>();
        stations.add(new Station3());
        stations.add(new Station4());

        bus.addEvent(new Event<>("test", stations));

        new CountDownLatch(1).await();
    }

    static class Station1 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) {
            System.out.printf("Station1: %s%n", event.getT());
        }
    }

    static class Station2 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) {
            System.out.printf("Station2: %s%n", event.getT());
        }
    }

    static class Station3 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) {
            System.out.printf("Station3: %s%n", event.getT());
        }
    }

    static class Station4 implements Station<String> {

        @Override
        public void debarkation(Event<String> event) {
            System.out.printf("Station4: %s%n", event.getT());
        }
    }
}
