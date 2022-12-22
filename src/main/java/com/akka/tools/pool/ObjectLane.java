package com.akka.tools.pool;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectLane<O> extends AbstractObjectPool<O> {
    public volatile Node<O> head;


    volatile Node<O> tail;


    private final ReentrantLock takeLock = new ReentrantLock();


    private final ReentrantLock putLock = new ReentrantLock();


    public ObjectLane(ObjectFactory<O> objectFactory) {
        super(objectFactory);
        head = tail = new Node<>();
    }

    @Override
    public O get() throws InterruptedException {
        takeLock.lockInterruptibly();
        try {
            if (isEmpty()) {
                Thread.yield();
                if (isEmpty()) {
                    return objectFactory.create();
                }
            }
            return dequeue();
        } finally {
            takeLock.unlock();
        }
    }


    @Override
    public void put(O o) throws InterruptedException {
        putLock.lockInterruptibly();
        try {
            enqueue(new Node<>(o));
        } finally {
            putLock.unlock();
        }
    }

    private boolean isEmpty() {
        return head == tail;
    }

    private O dequeue() {
        Node<O> h = head;
        Node<O> first = h.next;

        head = first;
        O o = first.o;
        first.o = null;

        h.next = h;
        return o;
    }


    private void enqueue(Node<O> node) {
        tail.next = node;
        tail = node;
    }


    static class Node<O> {
        O o;
        Node<O> next;

        public Node(O o, Node<O> next, Node<O> prev) {
            this.o = o;
            this.next = next;
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
