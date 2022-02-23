package com.akka.tools.bus;

import com.akka.tools.api.LifeCycle;

public interface Bus<T> extends LifeCycle {

    void addEvent(Event<T> event);

    void addStation(Station<T> station);
}
