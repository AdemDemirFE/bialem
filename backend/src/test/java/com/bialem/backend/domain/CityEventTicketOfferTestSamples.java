package com.bialem.backend.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CityEventTicketOfferTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CityEventTicketOffer getCityEventTicketOfferSample1() {
        return new CityEventTicketOffer()
            .id(1L)
            .providerCode("providerCode1")
            .externalOfferId("externalOfferId1")
            .sellerName("sellerName1")
            .purchaseUrl("purchaseUrl1")
            .currency("currency1")
            .priceLabel("priceLabel1");
    }

    public static CityEventTicketOffer getCityEventTicketOfferSample2() {
        return new CityEventTicketOffer()
            .id(2L)
            .providerCode("providerCode2")
            .externalOfferId("externalOfferId2")
            .sellerName("sellerName2")
            .purchaseUrl("purchaseUrl2")
            .currency("currency2")
            .priceLabel("priceLabel2");
    }

    public static CityEventTicketOffer getCityEventTicketOfferRandomSampleGenerator() {
        return new CityEventTicketOffer()
            .id(longCount.incrementAndGet())
            .providerCode(UUID.randomUUID().toString())
            .externalOfferId(UUID.randomUUID().toString())
            .sellerName(UUID.randomUUID().toString())
            .purchaseUrl(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .priceLabel(UUID.randomUUID().toString());
    }
}
