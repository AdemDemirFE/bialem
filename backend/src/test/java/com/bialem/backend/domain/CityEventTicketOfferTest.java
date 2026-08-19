package com.bialem.backend.domain;

import static com.bialem.backend.domain.CityEventTestSamples.*;
import static com.bialem.backend.domain.CityEventTicketOfferTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventTicketOfferTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventTicketOffer.class);
        CityEventTicketOffer cityEventTicketOffer1 = getCityEventTicketOfferSample1();
        CityEventTicketOffer cityEventTicketOffer2 = new CityEventTicketOffer();
        assertThat(cityEventTicketOffer1).isNotEqualTo(cityEventTicketOffer2);

        cityEventTicketOffer2.setId(cityEventTicketOffer1.getId());
        assertThat(cityEventTicketOffer1).isEqualTo(cityEventTicketOffer2);

        cityEventTicketOffer2 = getCityEventTicketOfferSample2();
        assertThat(cityEventTicketOffer1).isNotEqualTo(cityEventTicketOffer2);
    }

    @Test
    void cityEventTest() {
        CityEventTicketOffer cityEventTicketOffer = getCityEventTicketOfferRandomSampleGenerator();
        CityEvent cityEventBack = getCityEventRandomSampleGenerator();

        cityEventTicketOffer.setCityEvent(cityEventBack);
        assertThat(cityEventTicketOffer.getCityEvent()).isEqualTo(cityEventBack);

        cityEventTicketOffer.cityEvent(null);
        assertThat(cityEventTicketOffer.getCityEvent()).isNull();
    }
}
