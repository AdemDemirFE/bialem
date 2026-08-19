package com.bialem.backend.domain;

import static com.bialem.backend.domain.PartnerOfferRedemptionTestSamples.*;
import static com.bialem.backend.domain.PartnerOfferTestSamples.*;
import static com.bialem.backend.domain.PartnerVenueTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartnerOfferRedemptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerOfferRedemption.class);
        PartnerOfferRedemption partnerOfferRedemption1 = getPartnerOfferRedemptionSample1();
        PartnerOfferRedemption partnerOfferRedemption2 = new PartnerOfferRedemption();
        assertThat(partnerOfferRedemption1).isNotEqualTo(partnerOfferRedemption2);

        partnerOfferRedemption2.setId(partnerOfferRedemption1.getId());
        assertThat(partnerOfferRedemption1).isEqualTo(partnerOfferRedemption2);

        partnerOfferRedemption2 = getPartnerOfferRedemptionSample2();
        assertThat(partnerOfferRedemption1).isNotEqualTo(partnerOfferRedemption2);
    }

    @Test
    void offerTest() {
        PartnerOfferRedemption partnerOfferRedemption = getPartnerOfferRedemptionRandomSampleGenerator();
        PartnerOffer partnerOfferBack = getPartnerOfferRandomSampleGenerator();

        partnerOfferRedemption.setOffer(partnerOfferBack);
        assertThat(partnerOfferRedemption.getOffer()).isEqualTo(partnerOfferBack);

        partnerOfferRedemption.offer(null);
        assertThat(partnerOfferRedemption.getOffer()).isNull();
    }

    @Test
    void venueTest() {
        PartnerOfferRedemption partnerOfferRedemption = getPartnerOfferRedemptionRandomSampleGenerator();
        PartnerVenue partnerVenueBack = getPartnerVenueRandomSampleGenerator();

        partnerOfferRedemption.setVenue(partnerVenueBack);
        assertThat(partnerOfferRedemption.getVenue()).isEqualTo(partnerVenueBack);

        partnerOfferRedemption.venue(null);
        assertThat(partnerOfferRedemption.getVenue()).isNull();
    }

    @Test
    void userTest() {
        PartnerOfferRedemption partnerOfferRedemption = getPartnerOfferRedemptionRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        partnerOfferRedemption.setUser(profileBack);
        assertThat(partnerOfferRedemption.getUser()).isEqualTo(profileBack);

        partnerOfferRedemption.user(null);
        assertThat(partnerOfferRedemption.getUser()).isNull();
    }

    @Test
    void redeemedByTest() {
        PartnerOfferRedemption partnerOfferRedemption = getPartnerOfferRedemptionRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        partnerOfferRedemption.setRedeemedBy(profileBack);
        assertThat(partnerOfferRedemption.getRedeemedBy()).isEqualTo(profileBack);

        partnerOfferRedemption.redeemedBy(null);
        assertThat(partnerOfferRedemption.getRedeemedBy()).isNull();
    }
}
