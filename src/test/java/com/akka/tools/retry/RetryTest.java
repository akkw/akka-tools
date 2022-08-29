package com.akka.tools.retry;

import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.Documented;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryTest {

    @Test
    public void retryLastSuccessTest() throws Exception {
        int retryTime = 4;
        RetryPolicy retryPolicy = new RetryPolicy(retryTime, 100, "Retry Test", true);
        AtomicInteger size = new AtomicInteger();
        AtomicInteger value = new AtomicInteger(10);
        Random random = new Random();
        int retry = Retry.retry(retryPolicy, () -> {
            if (size.getAndIncrement() < retryTime -1) {
                throw new NullPointerException("test exception");
            }
            return value.get();
        });
        Assert.assertEquals(retry, value.get());
    }

    @Test(expected = NullPointerException.class)
    public void retryFailedTest() throws Exception {
        int retryTime = 4;
        RetryPolicy retryPolicy = new RetryPolicy(retryTime, 1000, "Retry Test", true);
        AtomicInteger size = new AtomicInteger();
        AtomicInteger value = new AtomicInteger(10);
        Retry.retry(retryPolicy, () -> {
            if (size.getAndIncrement() < retryTime + 1) {
                throw new NullPointerException("test exception");
            }
            return value.get();
        });
    }

    @Test
    public void foreverRetryTest() throws Exception {
        int retryTime = 4;
        RetryPolicy retryPolicy = new RetryPolicy(retryTime, 1000, "Retry Test", true);
        retryPolicy.setForeverRetry(true);
        AtomicInteger size = new AtomicInteger();
        AtomicInteger value = new AtomicInteger(10);
        Random random = new Random();
        int retry = Retry.retry(retryPolicy, () -> {
            if (random.nextInt(100) != 32) {
                throw new NullPointerException("test exception");
            }
            return value.get();
        });
        Assert.assertEquals(retry, value.get());
    }
}
