package com.akka.tools.pool;

import com.akka.tools.atomic.PaddedAtomicLong;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GeneralObjectPoolTest {

    long size = (long) Math.pow(10, 9);
    AtomicLong j = new PaddedAtomicLong();
    volatile int objectSize = 0;
    int getThreadSize = 1 << 2;
    int putThreadSize = 1;


    ThreadPoolExecutor executor = new ThreadPoolExecutor(getThreadSize + putThreadSize, getThreadSize + putThreadSize, 0,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    LinkedBlockingQueue<PoolTestObject> queue = new LinkedBlockingQueue<>();

    ThreadLocalRandom random = ThreadLocalRandom.current();
    @Test
    public void performanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        GeneralObjectPool<PoolTestObject> lanes = new GeneralObjectPool<>(5, this::createObject);
        Future<?>[] futures = new Future[getThreadSize];

        for (int i = 0; i < getThreadSize; i++) {
            futures[i] = executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                for (; j.get() < size; j.getAndIncrement()) {
                    try {
                        queue.add(lanes.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                long endTime = System.currentTimeMillis();
                return endTime - startTime;
            });
        }

        Future<Object> submit = executor.submit(() -> {
            for (; ; ) {
                PoolTestObject poll = null;
                try {
                    poll = queue.take();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (poll != null) {
                    lanes.put(poll);
                }
            }
        });

        for (int i = 0; i < getThreadSize; i++) {
            try {
                System.out.println(futures[i].get());
            } catch (Exception e) {
                throw e;
            }
        }

        System.out.printf("size: %d, create size: %d%n", size, objectSize);
        try {
            submit.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {

        }
    }


    private synchronized PoolTestObject createObject() {
//        System.out.println(1);
        return new PoolTestObject(objectSize++);
    }

}
