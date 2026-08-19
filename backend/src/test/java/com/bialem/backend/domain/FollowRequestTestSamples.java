package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class FollowRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FollowRequest getFollowRequestSample1() {
        return new FollowRequest().id(1L);
    }

    public static FollowRequest getFollowRequestSample2() {
        return new FollowRequest().id(2L);
    }

    public static FollowRequest getFollowRequestRandomSampleGenerator() {
        return new FollowRequest().id(longCount.incrementAndGet());
    }
}
