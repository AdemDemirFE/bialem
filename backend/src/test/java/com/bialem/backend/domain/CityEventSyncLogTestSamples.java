package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CityEventSyncLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static CityEventSyncLog getCityEventSyncLogSample1() {
        return new CityEventSyncLog().id(1L).providerCode("providerCode1").status("status1").importedCount(1);
    }

    public static CityEventSyncLog getCityEventSyncLogSample2() {
        return new CityEventSyncLog().id(2L).providerCode("providerCode2").status("status2").importedCount(2);
    }

    public static CityEventSyncLog getCityEventSyncLogRandomSampleGenerator() {
        return new CityEventSyncLog()
            .id(longCount.incrementAndGet())
            .providerCode(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString())
            .importedCount(intCount.incrementAndGet());
    }
}
