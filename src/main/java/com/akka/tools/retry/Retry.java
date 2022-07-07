package com.akka.tools.retry;


import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class Retry {


    public static <V> V retry(RetryPolicy policy, Callable<V> callable) throws Exception {
        if (policy == null || callable == null) {
            throw new NullPointerException();
        }
        int retryTime = 0;
        Exception exception;
        try {
            return callable.call();
        } catch (Exception e) {
            exception = e;
            while (policy.getTime() > retryTime++) {
                try {
                    return callable.call();
                } catch (Exception e1) {
                    if (policy.getTime() > retryTime++) {
                        throw e1;
                    }
                    TimeUnit.MILLISECONDS.sleep(policy.getIntervalTime());
                }
            }
        }
        throw exception;
    }
}
