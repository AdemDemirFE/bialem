package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CommentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Comment getCommentSample1() {
        return new Comment().id(1L).targetId("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa").body("body1");
    }

    public static Comment getCommentSample2() {
        return new Comment().id(2L).targetId("ad79f240-3727-46c3-b89f-2cf6ebd74367").body("body2");
    }

    public static Comment getCommentRandomSampleGenerator() {
        return new Comment().id(longCount.incrementAndGet()).targetId(UUID.randomUUID().toString()).body(UUID.randomUUID().toString());
    }
}
