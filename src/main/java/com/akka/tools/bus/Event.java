package com.akka.tools.bus;

import java.util.ArrayList;
import java.util.List;

public class Event<T> {
    T t;
    List<Station<T>> station;

    public Event(List<Station<T>> station) {
        this.station = station;
    }

    public Event(T t) {
        this.t = t;
    }

    public Event(T t, List<Station<T>> station) {
        this.t = t;
        this.station = station;
    }

    public T getT() {
        return t;
    }

    public List<Station<T>> getStation() {
        return station;
    }
}
