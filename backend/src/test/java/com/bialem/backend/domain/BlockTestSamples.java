package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class BlockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Block getBlockSample1() {
        return new Block().id(1L);
    }

    public static Block getBlockSample2() {
        return new Block().id(2L);
    }

    public static Block getBlockRandomSampleGenerator() {
        return new Block().id(longCount.incrementAndGet());
    }
}
