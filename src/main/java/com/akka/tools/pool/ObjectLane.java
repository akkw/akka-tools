package com.akka.tools.pool;

import com.akka.tools.atomic.PaddedAtomicInteger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectLane<O> extends AbstractObjectPool<O> {


    private final int PROBE_STEP = 1 << 16;

    private final int SPIN = 3 * PROBE_STEP;

    volatile Node<O> head;


    volatile Node<O> tail;


    private final ReentrantLock getLock = new ReentrantLock();


    private final ReentrantLock putLock = new ReentrantLock();


    private AtomicInteger count = new PaddedAtomicInteger();




    public ObjectLane(ObjectFactory<O> objectFactory) {
        super(objectFactory);
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public O get() throws InterruptedException {
        getLock.lockInterruptibly();
        try {
            int c = count.get();
            if (c == 0) {
                for (int i = 0; i < SPIN; i++) {
                    c = count.get();
                    if (c != 0) {
                        break;
                    }
                }
                if (c == 0) {
                    return objectFactory.create();
                }
            }
            O o = dequeue();

            count.getAndDecrement();
            return o;
        } finally {
            getLock.unlock();
        }
    }


    @Override
    public void put(O o) throws InterruptedException {
        putLock.lockInterruptibly();
        try {
            Node<O> node = new Node<>(o);

            enqueue(node);
            count.getAndIncrement();
        } finally {
            putLock.unlock();
        }
    }

    private O dequeue() {
        Node<O> h = head;
        Node<O> first = h.next;

        first.next.prev = h;
        h.next = first.next;

        first.next = first;
        first.prev = first;

        return first.o;
    }


    private void enqueue(Node<O> node) {
        final Node<O> t = this.tail;
        t.o = node.o;
        t.next = node;
        node.prev = t;
        node.o = null;
        tail = node;
    }


    public int size() {
        return count.get();
    }

    static class Node<O> {
        volatile O o;
        volatile Node<O> next;
        volatile Node<O> prev;

        public Node(O o, Node<O> next, Node<O> prev) {
            this.o = o;
            this.next = next;
            this.prev = prev;
        }

        public Node(O o) {
            this.o = o;
        }

        public Node() {
        }

        @Override
        public boolean equals(Object o1) {
            if (this == o1) return true;
            if (o1 == null || getClass() != o1.getClass()) return false;

            Node<?> node = (Node<?>) o1;

            return Objects.equals(o, node.o);
        }

        @Override
        public int hashCode() {
            return o != null ? o.hashCode() : 0;
        }
    }
}
