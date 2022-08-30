package com.akka.tools.pool;

public abstract class AbstractObjectPool<O> implements ObjectPool<O>{


    protected ObjectFactory<O> objectFactory;

    protected int width;

    protected int computeLane() {
        return hash(Thread.currentThread().getId());
    }




    private int hash(long threadId) {
        return (int) (0xFF & threadId);
    }
}
