package com.akka.tools.bus;

import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.LoggerFactory;
public class AbstractEventBus<T> implements Bus<T> {


    private static final Logger logger =  LoggerFactory.getLogger(AbstractEventBus.class);

    private static final int COUNT_BITS = 30;
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;
    /**
     * 公交车名.
     */
    protected final String busName;

    /**
     * 负责运送事件.
     */
    protected Thread bus;

    /**
     * 事件目的地.
     */
    protected final List<Station<T>> stations;

    /**
     * 事件.
     */
    protected final BlockingQueue<Event<T>> events;

    /**
     * 发车标记.
     */
    protected volatile boolean isRun;

    /**
     * 优雅关闭.
     */
    protected final boolean gracefulClose;


    protected boolean shutdown;



    public AbstractEventBus(String busName, boolean gracefulClose) {
        this.busName = busName;
        this.stations = new CopyOnWriteArrayList<>();
        this.events = new LinkedBlockingDeque<>();
        this.gracefulClose = gracefulClose;
    }


    @Override
    public void start() {
        if (isRun) {
           throw new RuntimeException(String.format("The %s bus has been started.", busName));
        }
        this.bus.start();
        logger.info("{} start success", busName);
    }

    @Override
    public void stop() {
        if (gracefulClose) {

        }
    }

    @Override
    public void addEvent(Event<T> event) {
        events.add(event);
    }

    @Override
    public void addStation(Station<T> station) {
        stations.add(station);
    }
}
