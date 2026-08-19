package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StoryCommunityTargetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StoryCommunityTarget getStoryCommunityTargetSample1() {
        return new StoryCommunityTarget().id(1L);
    }

    public static StoryCommunityTarget getStoryCommunityTargetSample2() {
        return new StoryCommunityTarget().id(2L);
    }

    public static StoryCommunityTarget getStoryCommunityTargetRandomSampleGenerator() {
        return new StoryCommunityTarget().id(longCount.incrementAndGet());
    }
}
