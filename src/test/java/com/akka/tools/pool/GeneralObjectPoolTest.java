package com.akka.tools.pool;

import com.akka.tools.atomic.PaddedAtomicLong;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GeneralObjectPoolTest {

    long size = (long) Math.pow(10, 8);
    AtomicLong j = new PaddedAtomicLong();
    volatile int objectSize = 0;
    int getThreadSize = 1;
    int putThreadSize = 1;


    ThreadPoolExecutor executor = new ThreadPoolExecutor(getThreadSize + putThreadSize, getThreadSize + putThreadSize, 0,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    LinkedBlockingQueue<ObjectLane.Node<PoolTestObject>> queue = new LinkedBlockingQueue<>();
    @Test
    public void performanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        GeneralObjectPool<PoolTestObject> lanes = new GeneralObjectPool<>(5, this::createObject);
        Future<?>[] futures = new Future[getThreadSize];

        for (int i = 0; i < getThreadSize; i++) {
            futures[i] = executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                for (; j.get() < size; j.getAndIncrement()) {
                    try {
                        ObjectLane.Node<PoolTestObject> poolTestObjectNode = lanes.get();
                        queue.add(poolTestObjectNode);
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
                ObjectLane.Node<PoolTestObject> poll = null;
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
                System.out.println("time: "+futures[i].get());
            } catch (Exception e) {
                throw e;
            }
        }

        System.out.printf("size: %d, create size: %d, ratio: %f", size, objectSize, ((double)objectSize / size));
        try {
            submit.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {

        }
    }


    private synchronized PoolTestObject createObject() {
        return  new PoolTestObject(objectSize++);
    }

}