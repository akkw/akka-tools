package com.akka.tools.cache;


import org.junit.Before;
import org.junit.Test;

public class AkkaLockTest {

    AkkaLock lock;


    @Before
    public void before() {
        lock = new AkkaLock();
    }


    @Test
    public void lockTest () {
        lock.lock();
        System.out.println("get the lock");
        lock.unlock();
    }

    @Test
    public void biasedTest () {
        for (int i = 0; i < 20; i++) {
            lock.lock();
            System.out.println("get the lock" + i);
            lock.unlock();
        }
    }

}
