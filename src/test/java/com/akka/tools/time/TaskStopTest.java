package com.akka.tools.time;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TaskStopTest {


    StopWatch watch = new StopWatch("junit-test");
    int taskNum = 7;
    CountDownLatch latch = new CountDownLatch(taskNum);
    Runnable r1 = ()->{
        try {
            watch.startTask(1L, "task1");
            TimeUnit.MILLISECONDS.sleep(2000L);
            watch.stopTask(1L);
            System.out.println("r1" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };

    Runnable r2 = ()->{
        try {
            TimeUnit.MILLISECONDS.sleep(1999L);
            watch.stopTask(1L);
            System.out.println("r2" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };

    Runnable r3 = ()->{
        try {
            TimeUnit.MILLISECONDS.sleep(1999L);
            watch.stopTask(1L);
            System.out.println("r3" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };

    Runnable r4 = ()->{
        try {
            TimeUnit.MILLISECONDS.sleep(2001L);
            watch.stopTask(1L);
            System.out.println("r4" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };

    Runnable r5 = ()->{
        try {
            TimeUnit.MILLISECONDS.sleep(2001L);
            watch.stopTask(1L);
            System.out.println("r5" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };

    Runnable r6 = ()->{
        try {
            TimeUnit.MILLISECONDS.sleep(1998L);
            watch.stopTask(1L);
            System.out.println("r6" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };

    Runnable r7 = ()->{
        try {
            TimeUnit.MILLISECONDS.sleep(1998L);
            watch.stopTask(1L);
            System.out.println("r7" + watch.prettyPrint());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }
    };


    @Test
    public void stopTaskTest() throws InterruptedException {
        new Thread(r1).start();
        new Thread(r2).start();
        new Thread(r3).start();
        new Thread(r4).start();
        new Thread(r5).start();
        new Thread(r6).start();
        new Thread(r7).start();
        latch.await();
    }
}
