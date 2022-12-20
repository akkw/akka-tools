package com.akka.tools.pool;

public interface ObjectPool<O> {

    ObjectLane.Node<O> get() throws InterruptedException;


    void put(ObjectLane.Node<O> o) throws InterruptedException;
}
