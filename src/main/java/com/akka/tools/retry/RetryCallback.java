package com.akka.tools.retry;

@FunctionalInterface
public interface RetryCallback {
    void run() throws Exception;
}
