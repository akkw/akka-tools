package com.akka.tools.bus;
/*
    create qiangzhiwei time 2022/8/27
 */

public class HeartbeatExecutor implements Runnable {

    private final BusHeartbeat heartbeat;

    public HeartbeatExecutor(BusHeartbeat heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public void run() {
        if (heartbeat != null) {
            heartbeat.doHeartbeat();
        }
    }

}
