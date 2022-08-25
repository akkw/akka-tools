package com.akka.tools.atomic;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PaddedAtomicLongTest {
    long size = (long) Math.pow(10, 9.2);

    PaddedAtomicLong lp1 = new PaddedAtomicLong();
    PaddedAtomicLong lp2 = new PaddedAtomicLong();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
    AtomicLong l1 = new AtomicLong();
    AtomicLong l2 = new AtomicLong();





    @Test
    public void atomicPaddedLong() throws InterruptedException, ExecutionException {
        Future<Long> submit1 = executor.submit(new AtomicWorker(lp1, size));
        Future<Long> submit2 = executor.submit(new AtomicWorker(lp2, size));
        Future<Long> submit3 = executor.submit(new AtomicWorker(l1, size));
        Future<Long> submit4 = executor.submit(new AtomicWorker(l2, size));
        Assert.assertTrue((submit1.get() << 2) < submit3.get());
        Assert.assertTrue((submit2.get() << 2) < submit4.get());
    }


    static class AtomicWorker implements Callable<Long> {
        AtomicLong l;
        long size;

        public AtomicWorker(AtomicLong l, long size) {
            this.l = l;
            this.size = size;
        }


        @Override
        public Long call() throws Exception {
            long start = System.currentTimeMillis();
            for (int i = 0; i < size; i++) {
                this.l.getAndIncrement();
            }
            long end = System.currentTimeMillis();
            return end - start;
        }
    }
}
