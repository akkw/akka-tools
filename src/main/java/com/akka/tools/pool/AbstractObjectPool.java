package com.akka.tools.pool;

import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractObjectPool<O> implements ObjectPool<O> {

    ThreadLocalRandom random = ThreadLocalRandom.current();
    protected ObjectFactory<O> objectFactory;
    protected final int laneWidth;

    public AbstractObjectPool(ObjectFactory<O> objectFactory) {
        this(0, objectFactory);
    }

    public AbstractObjectPool(int laneWidth, ObjectFactory<O> objectFactory) {
        if (objectFactory == null) {
            throw new NullPointerException("objectFactory is null");
        }
        this.laneWidth = laneWidth;
        this.objectFactory = objectFactory;
    }


    protected int computeLane() {
        return hash(random.nextInt(100));
    }


    private int hash(long key) {
        key += ~(key << 15);
        key ^= (key >>> 10);
        key += (key << 3);
        key ^= (key >>> 6);
        key += ~(key << 11);
        key ^= (key >>> 16);

        return Math.abs((int)key) % laneWidth;
    }
}
