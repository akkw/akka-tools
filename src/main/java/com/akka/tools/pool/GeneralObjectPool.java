package com.akka.tools.pool;


public class GeneralObjectPool<O> extends AbstractObjectPool<O> implements ObjectPool<O> {


    private final ObjectLane<O>[] lanes;


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
    public ObjectLane.Node<O> get() throws InterruptedException {
        int laneId = computeLane();
        return lanes[laneId].get();
    }

    @Override
    public void put(ObjectLane.Node<O> o) throws InterruptedException {
        int laneId = computeLane();
        lanes[laneId].put(o);
    }

}
