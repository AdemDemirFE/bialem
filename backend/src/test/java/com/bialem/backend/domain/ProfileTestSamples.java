package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Profile getProfileSample1() {
        return new Profile().id(1L).displayName("displayName1").username("username1").avatarUrl("avatarUrl1").bio("bio1").city("city1");
    }

    public static Profile getProfileSample2() {
        return new Profile().id(2L).displayName("displayName2").username("username2").avatarUrl("avatarUrl2").bio("bio2").city("city2");
    }

    public static Profile getProfileRandomSampleGenerator() {
        return new Profile()
            .id(longCount.incrementAndGet())
            .displayName(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .avatarUrl(UUID.randomUUID().toString())
            .bio(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString());
    }
}
