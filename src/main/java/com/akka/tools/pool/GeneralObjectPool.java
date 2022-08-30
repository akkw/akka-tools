package com.akka.tools.pool;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneralObjectPool<O> extends AbstractObjectPool<O> implements ObjectPool<O>{



    private final ObjectLane<O>[] lanes;



    @SuppressWarnings(value = {"unchecked"})
    public GeneralObjectPool(int laneWidth) {
        this.lanes = (ObjectLane<O>[]) new ObjectLane[laneWidth];
    }

    @Override
    public O get() {
        int laneId = computeLane();
        synchronized (lanes[laneId]) {
            return lanes[laneId].get();
        }
    }

    @Override
    public void put(O o) {
        int laneId = computeLane();
        synchronized (lanes[laneId]) {
            lanes[laneId].put(o);
        }
    }
}
