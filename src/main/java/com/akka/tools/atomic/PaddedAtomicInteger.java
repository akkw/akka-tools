package com.akka.tools.atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class PaddedAtomicInteger extends AtomicInteger {
    public volatile long p1, p2, p3, p4, p5, p6;
    public volatile int p7;
}
