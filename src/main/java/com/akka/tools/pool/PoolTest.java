package com.akka.tools.pool;

import com.akka.tools.atomic.PaddedAtomicLong;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class PoolTest {
    long size = (long) Math.pow(10, 8);
    AtomicLong j = new PaddedAtomicLong();
    volatile int objectSize = 0;
    int getThreadSize = 4;
    int putThreadSize = 1;


    ThreadPoolExecutor executor = new ThreadPoolExecutor(getThreadSize + putThreadSize, getThreadSize + putThreadSize, 0,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    LinkedBlockingQueue<PoolTestObject> queue = new LinkedBlockingQueue<>();
    GeneralObjectPool<PoolTestObject> lanes = new GeneralObjectPool<>(4, this::createObject);
    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
        PoolTest test = new PoolTest();
        test.performanceTest();
        int i = 0;
        ObjectLane.Node<PoolTestObject> head = test.lanes.lanes[0].head;
        ObjectLane.Node<PoolTestObject> next = head.next;
        while (next != null) {
            next = next.next;
            i++;
        }
        System.out.println("iiii" + i);
        test.executor.shutdownNow();
    }
    public void performanceTest() throws ExecutionException, InterruptedException, TimeoutException {

        Future<?>[] futures = new Future[getThreadSize];

        for (int i = 0; i < getThreadSize; i++) {
            futures[i] = executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                for (; j.get() < size; j.getAndIncrement()) {
                    try {
                        if (j.get() % (long) Math.pow(10, 7) == 0) {
                            System.out.println(j.get());
                        }
                        PoolTestObject poolTestObjectNode = lanes.get();
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
                System.out.println("time: "+futures[i].get());
            } catch (Exception e) {
                throw e;
            }
        }

        System.out.printf("size: %d, create size: %d, ratio: %f\n", size, objectSize, ((double)objectSize / size));
        try {
            submit.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {

        }
    }


    private synchronized PoolTestObject createObject() {
        System.out.println("createObject: "+ objectSize);
        return  new PoolTestObject(objectSize++);
    }
}