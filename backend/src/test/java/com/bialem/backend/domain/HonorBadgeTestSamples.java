package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HonorBadgeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static HonorBadge getHonorBadgeSample1() {
        return new HonorBadge().id(1L).code("code1").nameTemplate("nameTemplate1").description("description1").minimumCheckIns(1);
    }

    public static HonorBadge getHonorBadgeSample2() {
        return new HonorBadge().id(2L).code("code2").nameTemplate("nameTemplate2").description("description2").minimumCheckIns(2);
    }

    public static HonorBadge getHonorBadgeRandomSampleGenerator() {
        return new HonorBadge()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .nameTemplate(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .minimumCheckIns(intCount.incrementAndGet());
    }
}
