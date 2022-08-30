package com.akka.tools.pool;

public class ObjectLane<O> extends AbstractObjectPool<O> {





    @Override
    public O get() {
        return null;
    }

    @Override
    public void put(O o) {

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

        public Node(Node<O> next) {
            this.next = next;
        }
    }
}
