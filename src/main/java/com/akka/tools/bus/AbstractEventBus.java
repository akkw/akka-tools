package com.akka.tools.bus;

import com.akka.tools.atomic.PaddedAtomicInteger;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.LoggerFactory;


public class AbstractEventBus<T> implements Bus<T> {


    private static final Logger logger = LoggerFactory.getLogger(AbstractEventBus.class);

    private static final int RUNNING = 0xF000000;
    private static final int READY = 0xFF0000;
    private static final int SHUTDOWN = 0xFF00;
    private static final int SHUTDOWN_NOW = 0xFF;


    private final PaddedAtomicInteger ctl;




    protected void running() {
        int ready;
        do {
            ready = ctl.get();
            if (ready != READY) {
                throw new RuntimeException();
            }
        } while (!ctl.compareAndSet(ready, RUNNING));
    }

    protected void doShutdown() {
        int running;
        do {
            running = ctl.get();

            if (running != RUNNING) {
                throw new RuntimeException("不是运行状态");
            }
        } while (!ctl.compareAndSet(running, SHUTDOWN));
    }


    protected void doShutdownNow() {
        int running;
        do {
            running = ctl.get();
            if (running != RUNNING) {
                throw new RuntimeException("不是运行状态");
            }
        } while (!ctl.compareAndSet(running, SHUTDOWN_NOW));
    }


    protected boolean isRunning() {
        return RUNNING == ctl.get();
    }

    private boolean isReady() {
        return ctl.get() == READY;
    }

    protected boolean isShutdown() {
        return ctl.get() < SHUTDOWN + 1;
    }

    protected boolean isShutdownNow() {
        return ctl.get() < SHUTDOWN_NOW + 1;
    }




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


    public AbstractEventBus(String busName) {
        this.busName = busName;
        this.stations = new CopyOnWriteArrayList<>();
        this.events = new LinkedBlockingDeque<>();
        this.ctl = new PaddedAtomicInteger(READY);
    }


    @Override
    public void start() {
        if (isRunning()) {
            throw new RuntimeException(String.format("The %s bus has been started.", busName));
        }

        if (isReady()) {
            running();
        }
        this.bus.start();
        logger.info("{} start success", busName);
    }

    @Override
    public void addEvent(Event<T> event) {
        if (isShutdown()) {
            throw new RuntimeException("早已关闭");
        }
        events.add(event);
    }

    @Override
    public void addStation(Station<T> station) {
        if (isShutdown()) {
            throw new RuntimeException("早已关闭");
        }
        stations.add(station);
    }

    @Override
    public void shutdown() {
        if (!isRunning()) {
            throw new RuntimeException();
        }
        doShutdown();
    }

    @Override
    public void shutdownNow() {
        if (!isRunning()) {
            throw new RuntimeException();
        }
        doShutdownNow();
    }
}
