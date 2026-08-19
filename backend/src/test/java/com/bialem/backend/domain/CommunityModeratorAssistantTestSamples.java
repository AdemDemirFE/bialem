package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CommunityModeratorAssistantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CommunityModeratorAssistant getCommunityModeratorAssistantSample1() {
        return new CommunityModeratorAssistant().id(1L);
    }

    public static CommunityModeratorAssistant getCommunityModeratorAssistantSample2() {
        return new CommunityModeratorAssistant().id(2L);
    }

    public static CommunityModeratorAssistant getCommunityModeratorAssistantRandomSampleGenerator() {
        return new CommunityModeratorAssistant().id(longCount.incrementAndGet());
    }
}
