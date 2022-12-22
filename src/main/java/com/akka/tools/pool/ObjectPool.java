package com.akka.tools.pool;

public interface ObjectPool<O> {

    O get() throws InterruptedException;


    void put(O o) throws InterruptedException;
}
