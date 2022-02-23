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
