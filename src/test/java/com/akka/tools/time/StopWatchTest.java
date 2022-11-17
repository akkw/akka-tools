package com.akka.tools.time;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StopWatchTest {
    private final StopWatch watch = new StopWatch("junit-watch");
    private final String taskName1 = "task1";
    private final String taskName2 = "task2";
    private final String taskName3 = "task3";
    private final String taskName4 = "task4";

    private int taskNum = 4;

    private CountDownLatch latch = new CountDownLatch(4);
    Runnable r1 = () -> {
        try {
            watch.startTask(1L, taskName1);
            TimeUnit.MILLISECONDS.sleep(1000);
            watch.stopTask(1L);
            latch.countDown();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    Runnable r2 = () -> {
        try {
            watch.startTask(2L, taskName2);
            TimeUnit.MILLISECONDS.sleep(2000);
            watch.stopTask(2L);
            latch.countDown();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    Runnable r3 = () -> {
        try {
            watch.startTask(3L, taskName3);
            TimeUnit.MILLISECONDS.sleep(3000);
            watch.stopTask(3L);
            latch.countDown();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    Runnable r4 = () -> {
        try {
            watch.startTask(4L, taskName4);
            TimeUnit.MILLISECONDS.sleep(100000);
            watch.stopTask(4L);
            latch.countDown();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    };

    @Before
    public void before() throws InterruptedException {

        new Thread(r1).start();
        new Thread(r2).start();
        new Thread(r3).start();
        new Thread(r4).start();
        latch.await();
    }

    @Test
    public void testShortSummary() {
        System.out.println(watch.shortSummary());
    }

    @Test
    public void testPrettyPrint() {
        System.out.println(watch.prettyPrint());
    }

    public void stopWatchTest() {

    }
}