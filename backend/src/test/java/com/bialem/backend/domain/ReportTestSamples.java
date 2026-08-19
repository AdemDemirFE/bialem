package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Report getReportSample1() {
        return new Report().id(1L).targetId("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa").reason("reason1");
    }

    public static Report getReportSample2() {
        return new Report().id(2L).targetId("ad79f240-3727-46c3-b89f-2cf6ebd74367").reason("reason2");
    }

    public static Report getReportRandomSampleGenerator() {
        return new Report().id(longCount.incrementAndGet()).targetId(UUID.randomUUID().toString()).reason(UUID.randomUUID().toString());
    }
}
