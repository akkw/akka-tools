package com.akka.tools.atomic;

import java.util.concurrent.atomic.AtomicLong;

public class PaddedAtomicLong extends AtomicLong {

    public volatile long p1, p2, p3, p4, p5, p6= 7L;


    public PaddedAtomicLong() {
    }

    public PaddedAtomicLong(long initialValue) {
        super(initialValue);
    }
}
