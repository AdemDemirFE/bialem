package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CityEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CityEvent getCityEventSample1() {
        return new CityEvent()
            .id(1L)
            .title("title1")
            .category("category1")
            .city("city1")
            .venueName("venueName1")
            .addressText("addressText1")
            .coverImageUrl("coverImageUrl1")
            .priceLabel("priceLabel1")
            .sourceName("sourceName1")
            .sourceUrl("sourceUrl1")
            .ticketUrl("ticketUrl1")
            .providerCode("providerCode1")
            .externalId("externalId1");
    }

    public static CityEvent getCityEventSample2() {
        return new CityEvent()
            .id(2L)
            .title("title2")
            .category("category2")
            .city("city2")
            .venueName("venueName2")
            .addressText("addressText2")
            .coverImageUrl("coverImageUrl2")
            .priceLabel("priceLabel2")
            .sourceName("sourceName2")
            .sourceUrl("sourceUrl2")
            .ticketUrl("ticketUrl2")
            .providerCode("providerCode2")
            .externalId("externalId2");
    }

    public static CityEvent getCityEventRandomSampleGenerator() {
        return new CityEvent()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .venueName(UUID.randomUUID().toString())
            .addressText(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString())
            .priceLabel(UUID.randomUUID().toString())
            .sourceName(UUID.randomUUID().toString())
            .sourceUrl(UUID.randomUUID().toString())
            .ticketUrl(UUID.randomUUID().toString())
            .providerCode(UUID.randomUUID().toString())
            .externalId(UUID.randomUUID().toString());
    }
}
