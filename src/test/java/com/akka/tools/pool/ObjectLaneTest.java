package com.akka.tools.pool;

import com.akka.tools.atomic.PaddedAtomicLong;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ObjectLaneTest {

    long size = (long) Math.pow(10, 9);

    AtomicLong j = new PaddedAtomicLong();
    volatile int objectSize = 0;
    int getThreadSize = 1 << 2;
    int putThreadSize = 1;


    ThreadPoolExecutor executor = new ThreadPoolExecutor(getThreadSize + putThreadSize, getThreadSize + putThreadSize, 0,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
    LinkedBlockingQueue<ObjectLane.Node<PoolTestObject>> queue = new LinkedBlockingQueue<>();


    @Test
    public void performanceTest() throws ExecutionException, InterruptedException, TimeoutException {
        ObjectLane<PoolTestObject> lanes = new ObjectLane<>(this::createObject);
        Future<?>[] futures = new Future[getThreadSize];

        for (int i = 0; i < getThreadSize; i++) {
            futures[i] = executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                for (; j.get() < size; j.getAndIncrement()) {
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
        Assert.assertEquals(objectSize, prevSet.size());

        Object[] nextObject = nextSet.toArray();
        Object[] prevObject = prevSet.toArray();


        Assert.assertEquals(nextObject.length, prevObject.length);
        for (int i = 0; i < prevSet.size(); i++) {
            Assert.assertEquals(nextObject[i], prevObject[prevSet.size() - (1 + i)]);
        }
        System.out.printf("size: %d, create size: %d%n", size, objectSize);
    }

    @Test
    public void objectLaneTest () throws InterruptedException {
        ObjectLane<Integer> lane = new ObjectLane<>(this::createInt);
        ObjectLane.Node<Integer> node = lane.get();
        ObjectLane.Node<Integer> node1 = lane.get();
        ObjectLane.Node<Integer> node2 = lane.get();
        lane.put(node);
        lane.put(node1);
        lane.put(node2);

        node = lane.get();
        node1 = lane.get();
        node2 = lane.get();

        lane.put(node);
        lane.put(node1);
        lane.put(node2);
    }


    private synchronized PoolTestObject createObject() {
//        System.out.println(1);
        return new PoolTestObject(objectSize++);
    }
    int i;
    private synchronized Integer createInt() {
//        System.out.println(1);
        return i++;
    }

}
