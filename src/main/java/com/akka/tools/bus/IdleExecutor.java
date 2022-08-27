package com.akka.tools.bus;/* 
    create qiangzhiwei time 2022/8/27
 */

public class IdleExecutor implements Runnable{
    private final BusIdle idle;


    public IdleExecutor(BusIdle idle) {
        this.idle = idle;
    }


    @Override
    public void run() {
        if (idle != null) {
            idle.odIdle();
        }
    }
}
