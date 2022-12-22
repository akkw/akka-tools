package com.akka.tools.pool;


public class GeneralObjectPool<O> extends AbstractObjectPool<O> implements ObjectPool<O> {


    public final ObjectLane<O>[] lanes;


    @SuppressWarnings(value = {"unchecked"})
    public GeneralObjectPool(int laneWidth, ObjectFactory<O> objectFactory) {
        super(laneWidth, objectFactory);
        this.lanes = (ObjectLane<O>[]) new ObjectLane[laneWidth];
        initLanes();
    }

    private void initLanes() {
        for (int i = 0; i < laneWidth; i++) {
            this.lanes[i] = new ObjectLane<O>(objectFactory);
        }
    }

    @Override
    public O get() throws InterruptedException {
        int laneId = computeLane();
        return lanes[laneId].get();
    }

    @Override
    public void put(O o) throws InterruptedException {
        int laneId = computeLane();
        lanes[laneId].put(o);
    }

}
