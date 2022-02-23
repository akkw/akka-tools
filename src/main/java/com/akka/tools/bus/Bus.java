package com.akka.tools.bus;/* 
    create qiangzhiwei time 2022/2/22
 */

import com.akka.tools.api.LifeCycle;

public interface Bus<T> extends LifeCycle {

    void addEvent(Event<T> event);

    void addStation(Station<T> station);
}
