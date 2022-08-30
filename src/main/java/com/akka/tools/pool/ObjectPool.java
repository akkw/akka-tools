package com.akka.tools.pool;

public interface ObjectPool<O> {

    O get();


    void put(O o);
}
