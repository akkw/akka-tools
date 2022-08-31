package com.akka.tools.pool;

import java.util.concurrent.locks.ReentrantLock;

public class ObjectLane<O> extends AbstractObjectPool<O> {


    private volatile Node<O> head;


    private volatile Node<O> tail;


    private final ReentrantLock getLock = new ReentrantLock();


    private final ReentrantLock putLock = new ReentrantLock();



    @Override
    public O get() throws InterruptedException {
        getLock.lockInterruptibly();
        try {
            if (head.next == tail) {
                 return objectFactory.create();
            }
            return dequeue();
        } finally {
            getLock.unlock();
        }
    }


    private O dequeue() {
        Node<O> h = head;
        Node<O> first = h.next;

        h = first.next.prev;
        h.next = first.next;

        return first.o;
    }

    @Override
    public void put(O o) throws InterruptedException {
        putLock.lockInterruptibly();
        try {


            Node<O> node = new Node<>(o);

            enqueue(node);
        } finally {
            putLock.unlock();
        }
    }

    private void enqueue(Node<O> node) {
        final Node<O> t = this.tail;
        t.o = node.o;
        t.next = node;
        node.prev = t;
        node.o = null;
        tail = node;
    }


    static class Node<O> {
        O o;
        Node<O> next;
        Node<O> prev;

        public Node(O o, Node<O> next, Node<O> prev) {
            this.o = o;
            this.next = next;
            this.prev = prev;
        }

        public Node(O o) {
            this.o = o;
        }
    }
}
