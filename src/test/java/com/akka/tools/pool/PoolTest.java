package com.akka.tools.pool;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.junit.Test;

import java.util.Timer;
import java.util.concurrent.*;

public class PoolTest {
    final long SIZE = (long) Math.pow(10, 8);

    @Test
    public void poolTest() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));
        BlockingQueue<Event<String>> queue = new LinkedBlockingQueue<>();
        GenericObjectPool<Event<String>> pool = new GenericObjectPool<>(new BasePoolableObjectFactory<Event<String>>() {
            @Override
            public Event<String> makeObject() throws Exception {
                System.out.println("1");
                return new Event<>("test");
            }
        });
        Future<Long> submit = executor.submit(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                long startTime = 0, endTime = 0;

                try {
                    startTime = System.currentTimeMillis();
                    for (long i = 0; i < SIZE; i++) {
                        Event<String> event = pool.borrowObject();
                        if (event == null) {
                            System.out.println("event is null");
                        } else {
                            queue.add(event);
                        }

                    }
                    endTime = System.currentTimeMillis();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return endTime - startTime;
            }
        });
        executor.execute(() -> {
            try {
                for (; ; ) {
                    pool.returnObject(queue.poll(10, TimeUnit.MILLISECONDS));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println(submit.get());
    }


    @Test
    public void newObjectTest() {
        BlockingQueue<Event<String>> queue = new LinkedBlockingQueue<>();

        new Thread(()-> {
            int i = 0;
            try {
                for (;;) {
                    queue.take();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        long startTime = System.currentTimeMillis();
        for (long i = 0; i < SIZE; i++) {
            queue.add(new Event<>("test"));
        }
        long endTime = System.currentTimeMillis();

        System.out.println(endTime - startTime);
    }


    static class Event<T> {
        T value;

        public Event(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "value=" + value +
                    '}';
        }
    }
}
