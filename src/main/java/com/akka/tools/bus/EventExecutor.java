package com.akka.tools.bus;

import java.util.List;

public class EventExecutor<T> implements Runnable {
    private final Event<T> event;
    private final List<Station<T>> stations;

    public EventExecutor(Event<T> event, List<Station<T>> stations) {
        this.event = event;
        this.stations = stations;
    }

    public Event<T> getEvent() {
        return event;
    }

    @Override
    public void run() {
        try {
            // event 有指定的station 执行完即返回
            if (event.station != null) {

                for (Station<T> station : event.station) {
                    station.debarkation(event);
                }
                return;
            }


            for (Station<T> station : stations) {
                station.debarkation(event);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
