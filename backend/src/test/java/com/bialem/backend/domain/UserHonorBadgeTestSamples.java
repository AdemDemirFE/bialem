package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserHonorBadgeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserHonorBadge getUserHonorBadgeSample1() {
        return new UserHonorBadge().id(1L).reason("reason1");
    }

    public static UserHonorBadge getUserHonorBadgeSample2() {
        return new UserHonorBadge().id(2L).reason("reason2");
    }

    public static UserHonorBadge getUserHonorBadgeRandomSampleGenerator() {
        return new UserHonorBadge().id(longCount.incrementAndGet()).reason(UUID.randomUUID().toString());
    }
}
