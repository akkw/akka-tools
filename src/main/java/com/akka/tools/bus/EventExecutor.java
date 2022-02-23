/*
 * Copyright © 2022 akka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
