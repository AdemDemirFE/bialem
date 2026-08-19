package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PartnerVenueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PartnerVenue getPartnerVenueSample1() {
        return new PartnerVenue()
            .id(1L)
            .name("name1")
            .slug("slug1")
            .logoUrl("logoUrl1")
            .coverImageUrl("coverImageUrl1")
            .address("address1")
            .city("city1")
            .phone("phone1")
            .websiteUrl("websiteUrl1")
            .instagramUrl("instagramUrl1");
    }

    public static PartnerVenue getPartnerVenueSample2() {
        return new PartnerVenue()
            .id(2L)
            .name("name2")
            .slug("slug2")
            .logoUrl("logoUrl2")
            .coverImageUrl("coverImageUrl2")
            .address("address2")
            .city("city2")
            .phone("phone2")
            .websiteUrl("websiteUrl2")
            .instagramUrl("instagramUrl2");
    }

    public static PartnerVenue getPartnerVenueRandomSampleGenerator() {
        return new PartnerVenue()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .logoUrl(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .websiteUrl(UUID.randomUUID().toString())
            .instagramUrl(UUID.randomUUID().toString());
    }
}
