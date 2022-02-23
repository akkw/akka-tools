package com.akka.toles.bus;/* 
    create qiangzhiwei time 2022/2/23
 */

import com.akka.tools.bus.Event;
import com.akka.tools.bus.SingleEventBus;
import com.akka.tools.bus.Station;
import org.junit.Before;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
