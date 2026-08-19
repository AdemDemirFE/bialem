package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PartnerOfferTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static PartnerOffer getPartnerOfferSample1() {
        return new PartnerOffer().id(1L).title("title1").perUserLimit(1);
    }

    public static PartnerOffer getPartnerOfferSample2() {
        return new PartnerOffer().id(2L).title("title2").perUserLimit(2);
    }

    public static PartnerOffer getPartnerOfferRandomSampleGenerator() {
        return new PartnerOffer()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .perUserLimit(intCount.incrementAndGet());
    }
}
