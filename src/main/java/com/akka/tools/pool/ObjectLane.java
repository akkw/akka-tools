package com.akka.tools.pool;

import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ObjectLane<O> extends AbstractObjectPool<O> {
    volatile Node<O> head;


    volatile Node<O> tail;


    private final ReentrantLock takeLock = new ReentrantLock();


    private final ReentrantLock putLock = new ReentrantLock();



    public ObjectLane(ObjectFactory<O> objectFactory) {
        super(objectFactory);
        head = new Node<>();
        tail = new Node<>();
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public Node<O> get() throws InterruptedException {
        takeLock.lockInterruptibly();
        try {
            if (isEmpty()) {
                Thread.yield();
                if (isEmpty()) {
                    System.out.println("isEmpty");
                    return new Node<>(objectFactory.create());
                }
            }
            return dequeue();
        } finally {
            takeLock.unlock();
        }
    }


    @Override
    public void put(Node<O> o) throws InterruptedException {
        putLock.lockInterruptibly();
        try {
            enqueue(o);
        } finally {
            putLock.unlock();
        }
    }

    private boolean isEmpty() throws InterruptedException {
        takeLock.lockInterruptibly();
        try {
            return head.next == tail;
        } finally {
            takeLock.unlock();
        }
    }

    private Node<O> dequeue() throws InterruptedException {


    }


    private void enqueue(Node<O> node) {

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
