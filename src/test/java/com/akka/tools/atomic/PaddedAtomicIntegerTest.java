package com.akka.tools.atomic;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PaddedAtomicIntegerTest {

    PaddedAtomicInteger ip1 = new PaddedAtomicInteger();
    PaddedAtomicInteger ip2 = new PaddedAtomicInteger();
    AtomicInteger i1 = new AtomicInteger();
    AtomicInteger i2 = new AtomicInteger();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
    long size = (long) Math.pow(10, 9.2);

    @Test
    public void atomicPaddedInteger() throws InterruptedException, ExecutionException {
        Future<Long> submit1 = executor.submit(new AtomicWorker(ip1, size));
        Future<Long> submit2 = executor.submit(new AtomicWorker(ip2, size));
        Future<Long> submit3 = executor.submit(new AtomicWorker(i1, size));
        Future<Long> submit4 = executor.submit(new AtomicWorker(i2, size));
        Assert.assertTrue((submit1.get() << 2) < submit3.get());
        Assert.assertTrue((submit2.get() << 2) < submit4.get());
    }


    static class AtomicWorker implements Callable<Long> {
        AtomicInteger l;
        long size;

        public AtomicWorker(AtomicInteger l, long size) {
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
