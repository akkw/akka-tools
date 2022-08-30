package com.akka.tools.retry;

public class RetryPolicy {

    /**
     * 重试次数
     */
    private int time;
    /**
     * 两次重试之间的时间间隔
     */
    private long intervalTime;
    /**
     * 重试标识
     */
    private String name;
    /**
     * 是否打印错误日志
     */
    private boolean log;
    /**
     * 一直重试
     */
    private boolean foreverRetry;

    public RetryPolicy(int time, long intervalTime) {
        this.time = time;
        this.intervalTime = intervalTime;
    }

    public RetryPolicy() {
        this("Default-Retry");
    }

    public RetryPolicy(String name) {
        this(3, 100);
        this.name = name;
    }

    public RetryPolicy(boolean log) {
        this();
        this.log = log;
    }

    public RetryPolicy(int time, long intervalTime, String name, boolean log) {
        this(time, intervalTime);
        this.name = name;
        this.log = log;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public String getName() {
        return name;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isForeverRetry() {
        return foreverRetry;
    }

    public void setForeverRetry(boolean foreverRetry) {
        this.foreverRetry = foreverRetry;
    }
}
