package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PushTokenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PushToken getPushTokenSample1() {
        return new PushToken().id(1L).deviceToken("deviceToken1").deviceName("deviceName1");
    }

    public static PushToken getPushTokenSample2() {
        return new PushToken().id(2L).deviceToken("deviceToken2").deviceName("deviceName2");
    }

    public static PushToken getPushTokenRandomSampleGenerator() {
        return new PushToken()
            .id(longCount.incrementAndGet())
            .deviceToken(UUID.randomUUID().toString())
            .deviceName(UUID.randomUUID().toString());
    }
}
