package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UserReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UserReview getUserReviewSample1() {
        return new UserReview().id(1L).rating(1).reviewText("reviewText1");
    }

    public static UserReview getUserReviewSample2() {
        return new UserReview().id(2L).rating(2).reviewText("reviewText2");
    }

    public static UserReview getUserReviewRandomSampleGenerator() {
        return new UserReview().id(longCount.incrementAndGet()).rating(intCount.incrementAndGet()).reviewText(UUID.randomUUID().toString());
    }
}
