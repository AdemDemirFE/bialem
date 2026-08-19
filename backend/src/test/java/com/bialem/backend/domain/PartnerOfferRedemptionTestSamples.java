package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartnerOfferRedemptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PartnerOfferRedemption getPartnerOfferRedemptionSample1() {
        return new PartnerOfferRedemption()
            .id(1L)
            .token(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"))
            .redemptionCode("redemptionCode1");
    }

    public static PartnerOfferRedemption getPartnerOfferRedemptionSample2() {
        return new PartnerOfferRedemption()
            .id(2L)
            .token(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"))
            .redemptionCode("redemptionCode2");
    }

    public static PartnerOfferRedemption getPartnerOfferRedemptionRandomSampleGenerator() {
        return new PartnerOfferRedemption()
            .id(longCount.incrementAndGet())
            .token(UUID.randomUUID())
            .redemptionCode(UUID.randomUUID().toString());
    }
}
