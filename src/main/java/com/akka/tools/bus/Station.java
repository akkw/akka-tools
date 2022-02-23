package com.akka.tools.bus;

public interface Station<T> {
   void debarkation(Event<T> event);
}
