package com.akka.tools.atomic;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicPaddedLong extends AtomicLong {

    public volatile long p1, p2, p3, p4, p5, p6 = 7L;


    public AtomicPaddedLong() {
    }

    public AtomicPaddedLong(long initialValue) {
        super(initialValue);
    }
}
