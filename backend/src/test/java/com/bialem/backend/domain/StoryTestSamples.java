package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Story getStorySample1() {
        return new Story().id(1L).body("body1").mediaUrl("mediaUrl1");
    }

    public static Story getStorySample2() {
        return new Story().id(2L).body("body2").mediaUrl("mediaUrl2");
    }

    public static Story getStoryRandomSampleGenerator() {
        return new Story().id(longCount.incrementAndGet()).body(UUID.randomUUID().toString()).mediaUrl(UUID.randomUUID().toString());
    }
}
