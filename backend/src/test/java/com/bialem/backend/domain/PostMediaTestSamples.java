package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PostMediaTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PostMedia getPostMediaSample1() {
        return new PostMedia().id(1L).storagePath("storagePath1").sortOrder(1);
    }

    public static PostMedia getPostMediaSample2() {
        return new PostMedia().id(2L).storagePath("storagePath2").sortOrder(2);
    }

    public static PostMedia getPostMediaRandomSampleGenerator() {
        return new PostMedia()
            .id(longCount.incrementAndGet())
            .storagePath(UUID.randomUUID().toString())
            .sortOrder(intCount.incrementAndGet());
    }
}
