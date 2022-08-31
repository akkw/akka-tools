package com.akka.tools.pool;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ObjectLaneTest {

    long size = (long) Math.pow(10, 3);
    volatile int objectSize = 0;
    int getThreadSize = 1;
    int putThreadSize = 1;


    ThreadPoolExecutor executor = new ThreadPoolExecutor(getThreadSize + putThreadSize, getThreadSize + putThreadSize, 0,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    LinkedBlockingQueue<PoolTestObject> queue = new LinkedBlockingQueue<>();


    @Test
    public void performanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        ObjectLane<PoolTestObject> lanes = new ObjectLane<>(this::createObject);
        Future<?>[] futures = new Future[getThreadSize];

        for (int i = 0; i < getThreadSize; i++) {
            futures[i] = executor.submit(() -> {
                TimeUnit.SECONDS.sleep(2);
                long startTime = System.currentTimeMillis();
                for (int j = 0; j < size; j++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(2);
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
        try {
            submit.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {

        }
        System.out.printf("size: %d, create size: %d%n", size, objectSize);

        Assert.assertEquals(lanes.size(), objectSize);
        ObjectLane.Node<PoolTestObject> next;
        ObjectLane.Node<PoolTestObject> head = lanes.head;
        List<String> nextSet = new ArrayList<>(4000000);
        while ((next = head.next) != null && next != lanes.tail) {
            String s = next.toString();
            if (!nextSet.contains(s)) {
                nextSet.add(s);
            } else {
                System.out.println("next: " + s);
            }
            head.next = head.next.next;
        }
        Assert.assertEquals(nextSet.size(), lanes.size());
        Assert.assertEquals(objectSize, nextSet.size());

        ObjectLane.Node<PoolTestObject> prev;
        ObjectLane.Node<PoolTestObject> tail = lanes.tail;
        List<String> prevSet = new ArrayList<>(4000000);
        while ((prev = tail.prev) != null && prev != lanes.head) {
            String s = prev.toString();
            if (!prevSet.contains(s)) {
                prevSet.add(s);
            } else {
                System.out.println("prev: " + s);
            }
            tail.prev = tail.prev.prev;
        }
        Assert.assertEquals(prevSet.size(), lanes.size());
        Assert.assertEquals(objectSize, prevSet.size());

        Object[] nextObject = nextSet.toArray();
        Object[] prevObject = prevSet.toArray();


        Assert.assertEquals(nextObject.length, prevObject.length);
        for (int i = 0; i < prevSet.size(); i++) {
            Assert.assertEquals(nextObject[i], prevObject[prevSet.size() - (1 + i)]);
        }
        System.out.printf("size: %d, create size: %d%n", size, objectSize);
    }


    private synchronized PoolTestObject createObject() {
//        System.out.println(1);
        return new PoolTestObject(objectSize++);
    }

}
