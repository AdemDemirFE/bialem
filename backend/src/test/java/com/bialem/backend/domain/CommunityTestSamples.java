package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CommunityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Community getCommunitySample1() {
        return new Community().id(1L).name("name1").slug("slug1").coverImageUrl("coverImageUrl1");
    }

    public static Community getCommunitySample2() {
        return new Community().id(2L).name("name2").slug("slug2").coverImageUrl("coverImageUrl2");
    }

    public static Community getCommunityRandomSampleGenerator() {
        return new Community()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString());
    }
}
