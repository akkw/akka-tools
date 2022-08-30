package com.akka.tools.pool;

public interface ObjectFactory<O> {
    O create();
}
