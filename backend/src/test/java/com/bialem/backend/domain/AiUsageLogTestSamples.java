package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AiUsageLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AiUsageLog getAiUsageLogSample1() {
        return new AiUsageLog().id(1L);
    }

    public static AiUsageLog getAiUsageLogSample2() {
        return new AiUsageLog().id(2L);
    }

    public static AiUsageLog getAiUsageLogRandomSampleGenerator() {
        return new AiUsageLog().id(longCount.incrementAndGet());
    }
}
