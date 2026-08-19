package com.bialem.backend.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CityEventInterestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CityEventInterest getCityEventInterestSample1() {
        return new CityEventInterest().id(1L);
    }

    public static CityEventInterest getCityEventInterestSample2() {
        return new CityEventInterest().id(2L);
    }

    public static CityEventInterest getCityEventInterestRandomSampleGenerator() {
        return new CityEventInterest().id(longCount.incrementAndGet());
    }
}
