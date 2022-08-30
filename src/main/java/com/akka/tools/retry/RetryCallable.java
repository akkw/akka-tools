package com.akka.tools.retry;

@FunctionalInterface
public interface RetryCallable<V> {
    V call() throws Exception;
}
