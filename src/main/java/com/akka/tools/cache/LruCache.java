package com.akka.tools.cache;

public class LruCache<E> extends AkkaLock{

    volatile Node<E> head;
    volatile Node<E> last;

    volatile long size;
    volatile long capacity;
    static class Node<E> {
        E e;
        Node<E> next;
        Node<E> prev;


        public Node(Node<E> prev, E e, Node<E> next) {
            this.e = e;
            this.next = next;
            this.prev = prev;
        }

    }


    public Node<E> add(E e) {
        if (e == null)
            throw new NullPointerException();
        lock();
        linkFirst(e);
        if (size > capacity) {
            return unlinkLast(last);
        }
        unlock();
        return null;
    }

    public E get(E e) {
        if (e == null)
            throw new NullPointerException();
        lock();
        Node<E> n = node(e);
        unlink(n);
        linkFirst(n.e);
        unlock();
        return n.e;
    }

    Node<E> node(E e) {
        Node<E> next = null;
        for (Node<E> n = head; n != null; n = next) {
            E element = n.e;
            next = n.next;
            if (e.equals(element)) {
                return n;
            }
        }
        return null;
    }

    void linkLast(E e) {
        Node<E> l = last;
        Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null) {
            head = newNode;
        } else {
            l.next = newNode;
        }
        size++;
    }

    void linkFirst(E e) {
        Node<E> h = head;
        Node<E> newNode = new Node<>(null, e, h);
        head = newNode;

        if (h == null) {
            last = newNode;
        } else {
            h.prev = newNode;
        }

        size++;
    }

    void unlink(LruCache.Node<E> n) {

        Node<E> prev = n.prev;
        Node<E> next = n.next;


        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            n.prev = null;
        }


        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            n.next = null;
        }
    }


    Node<E> unlinkLast(LruCache.Node<E> n) {
        Node<E> prev = n.prev;

        n.e = null;
        n.prev = null;
        last = prev;

        if (prev == null) {
            head = null;
        } else {
            prev.next = null;
        }

        size--;
        return n;
    }
}
