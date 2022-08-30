package com.akka.tools.retry;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Retry {
    private static final Logger logger = LoggerFactory.getLogger(Retry.class);
    private static final RetryPolicy retryPolicy = new RetryPolicy(3, 100);

    public static void retry(RetryCallback runnable) throws Exception {
        retry(retryPolicy, runnable);
    }

    public static void retry(RetryPolicy policy, RetryCallback runnable) throws Exception {
        if (policy == null || runnable == null) {
            throw new NullPointerException();
        }
        Exception exception = null;
        int retryTime = 0;
        try {
            runnable.run();
        } catch (Exception e) {
            if (policy.isLog()) {
                logger.error("{} error", policy.getName(), e);
            }
            do {
                exception = null;
                TimeUnit.MILLISECONDS.sleep(policy.getIntervalTime());
                try {
                    runnable.run();
                    break;
                } catch (Exception e1) {
                    if (policy.isLog()) {
                        logger.error(String.format("Retry name: [%s] error", policy.getName()), e);
                    }
                    exception = e1;
                }
            } while (policy.isForeverRetry() || policy.getTime() > ++retryTime);
        }
        if (exception != null) {
            throw exception;
        }
    }

    public static <V> V retry(RetryCallable<V> callable) throws Exception {
        return retry(retryPolicy, callable);
    }

    public static <V> V retry(RetryPolicy policy, RetryCallable<V> callable) throws Exception {
        if (policy == null || callable == null) {
            throw new NullPointerException();
        }
        Exception exception = null;
        V v = null;
        int retryTime = 0;
        try {
            v = callable.call();
        } catch (Exception e) {
            if (policy.isLog()) {
                logger.error("{} error", policy.getName(), e);
            }
            do {
                exception = null;
                TimeUnit.MILLISECONDS.sleep(policy.getIntervalTime());
                try {
                    v = callable.call();
                    break;
                } catch (Exception e1) {
                    if (policy.isLog()) {
                        logger.error(String.format("Retry name: [%s] error", policy.getName()), e);
                    }
                    exception = e1;
                }
            } while (policy.isForeverRetry() || policy.getTime() > ++retryTime);
        }
        if (exception != null) {
            throw exception;
        }
        return v;
    }
}
