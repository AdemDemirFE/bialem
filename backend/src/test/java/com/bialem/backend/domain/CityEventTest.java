package com.bialem.backend.domain;

import static com.bialem.backend.domain.CityEventInterestTestSamples.*;
import static com.bialem.backend.domain.CityEventTestSamples.*;
import static com.bialem.backend.domain.CityEventTicketOfferTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CityEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEvent.class);
        CityEvent cityEvent1 = getCityEventSample1();
        CityEvent cityEvent2 = new CityEvent();
        assertThat(cityEvent1).isNotEqualTo(cityEvent2);

        cityEvent2.setId(cityEvent1.getId());
        assertThat(cityEvent1).isEqualTo(cityEvent2);

        cityEvent2 = getCityEventSample2();
        assertThat(cityEvent1).isNotEqualTo(cityEvent2);
    }

    @Test
    void interestsTest() {
        CityEvent cityEvent = getCityEventRandomSampleGenerator();
        CityEventInterest cityEventInterestBack = getCityEventInterestRandomSampleGenerator();

        cityEvent.addInterests(cityEventInterestBack);
        assertThat(cityEvent.getInterests()).containsOnly(cityEventInterestBack);
        assertThat(cityEventInterestBack.getCityEvent()).isEqualTo(cityEvent);

        cityEvent.removeInterests(cityEventInterestBack);
        assertThat(cityEvent.getInterests()).doesNotContain(cityEventInterestBack);
        assertThat(cityEventInterestBack.getCityEvent()).isNull();

        cityEvent.interests(new HashSet<>(Set.of(cityEventInterestBack)));
        assertThat(cityEvent.getInterests()).containsOnly(cityEventInterestBack);
        assertThat(cityEventInterestBack.getCityEvent()).isEqualTo(cityEvent);

        cityEvent.setInterests(new HashSet<>());
        assertThat(cityEvent.getInterests()).doesNotContain(cityEventInterestBack);
        assertThat(cityEventInterestBack.getCityEvent()).isNull();
    }

    @Test
    void ticketOffersTest() {
        CityEvent cityEvent = getCityEventRandomSampleGenerator();
        CityEventTicketOffer cityEventTicketOfferBack = getCityEventTicketOfferRandomSampleGenerator();

        cityEvent.addTicketOffers(cityEventTicketOfferBack);
        assertThat(cityEvent.getTicketOffers()).containsOnly(cityEventTicketOfferBack);
        assertThat(cityEventTicketOfferBack.getCityEvent()).isEqualTo(cityEvent);

        cityEvent.removeTicketOffers(cityEventTicketOfferBack);
        assertThat(cityEvent.getTicketOffers()).doesNotContain(cityEventTicketOfferBack);
        assertThat(cityEventTicketOfferBack.getCityEvent()).isNull();

        cityEvent.ticketOffers(new HashSet<>(Set.of(cityEventTicketOfferBack)));
        assertThat(cityEvent.getTicketOffers()).containsOnly(cityEventTicketOfferBack);
        assertThat(cityEventTicketOfferBack.getCityEvent()).isEqualTo(cityEvent);

        cityEvent.setTicketOffers(new HashSet<>());
        assertThat(cityEvent.getTicketOffers()).doesNotContain(cityEventTicketOfferBack);
        assertThat(cityEventTicketOfferBack.getCityEvent()).isNull();
    }
}
