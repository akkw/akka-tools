package com.akka.tools.cache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

class AkkaLock {


    // 是否为偏向锁
    private AtomicBoolean biased;




    private AtomicInteger count;


    private final Sync sync;


    public AkkaLock() {
        sync = new Sync();
        biased = new AtomicBoolean();
        count = new AtomicInteger();
    }

    public void lock() {
        sync.acquire(1);
    }

    public void unlock() {
        sync.release(1);
    }

    class Sync extends AbstractQueuedSynchronizer {


        // 偏向的线程引用
        private final AtomicReference<Thread> thread;

        public Sync() {
            this.thread = new AtomicReference<>();
        }

        @Override
        protected boolean tryAcquire(int arg) {
            if (isBiasedCurrentThread()) {
                return true;
            }
            return lightweightLock();
        }


        @Override
        protected boolean tryRelease(int arg) {
            return compareAndSetState(1, 0);
        }

        private boolean lightweightLock() {

            if (compareAndSetState(0, 1)) {
                // 只有一个线程可以进来
                if (thread.get() == null) {
                    thread.set(Thread.currentThread());
                } else {
                    Thread t = Thread.currentThread();
                    // 线程引用不一样说明有其他线程抢锁, 退出偏向模式
                    if (t.equals(this.thread.get())) {
                        int c = count.get();
                        boolean b = count.compareAndSet(c, c + 1);
                        if (!b) {
                            return false;
                        }
                    } else {
                        count.set(0);
                        biased.set(false);
                        thread.set(null);
                    }
                }

                // 判断是否可以进入偏向模式
                if (count.get() > 16) {
                    if (!biased.get()) {
                        biased.compareAndSet(false, true);
                    }
                }
                return true;
            }
            return false;
        }

        private boolean isBiasedCurrentThread() {
            if (biased.get()) {
                Thread thread = Thread.currentThread();

                if (thread.equals(this.thread.get())) {
                    return true;
                } else {
                    biased.set(false);
                }
            }
            return false;
        }
    }


}
