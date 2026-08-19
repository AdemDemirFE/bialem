package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class StoryViewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StoryView getStoryViewSample1() {
        return new StoryView().id(1L);
    }

    public static StoryView getStoryViewSample2() {
        return new StoryView().id(2L);
    }

    public static StoryView getStoryViewRandomSampleGenerator() {
        return new StoryView().id(longCount.incrementAndGet());
    }
}
