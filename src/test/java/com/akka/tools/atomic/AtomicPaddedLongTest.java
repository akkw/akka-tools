package com.akka.tools.atomic;

import com.baidu.fsg.uid.utils.PaddedAtomicLong;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicPaddedLongTest {
    long size =  (long)Math.pow(10, 9);


    AtomicPaddedLong lp1 = new AtomicPaddedLong();
    AtomicPaddedLong lp2 = new AtomicPaddedLong();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(2,2,0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));


    @Test
    public void atomicPaddedLong() throws InterruptedException {
        executor.execute(new AtomicWorker(lp1, size));
        executor.execute(new AtomicWorker(lp2, size));
        new CountDownLatch(1).await();
    }


    static class AtomicWorker implements Runnable{
        AtomicLong l;
        long size;

        public AtomicWorker(AtomicLong l,long size) {
            this.l = l;
            this.size = size;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                this.l.getAndIncrement();
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        }
    }
}
