package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EventRatingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static EventRating getEventRatingSample1() {
        return new EventRating().id(1L).rating(1).reviewText("reviewText1");
    }

    public static EventRating getEventRatingSample2() {
        return new EventRating().id(2L).rating(2).reviewText("reviewText2");
    }

    public static EventRating getEventRatingRandomSampleGenerator() {
        return new EventRating()
            .id(longCount.incrementAndGet())
            .rating(intCount.incrementAndGet())
            .reviewText(UUID.randomUUID().toString());
    }
}
