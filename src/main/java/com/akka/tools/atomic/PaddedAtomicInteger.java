package com.akka.tools.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class PaddedAtomicInteger extends AtomicInteger {
    private volatile long p1, p2, p3, p4, p5, p6;
    private volatile int p7;

    public PaddedAtomicInteger(int initialValue) {
        super(initialValue);
    }

    public PaddedAtomicInteger() {
    }
}
