package com.bialem.backend.domain;

import static com.bialem.backend.domain.PartnerOfferRedemptionTestSamples.*;
import static com.bialem.backend.domain.PartnerOfferTestSamples.*;
import static com.bialem.backend.domain.PartnerVenueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PartnerOfferTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerOffer.class);
        PartnerOffer partnerOffer1 = getPartnerOfferSample1();
        PartnerOffer partnerOffer2 = new PartnerOffer();
        assertThat(partnerOffer1).isNotEqualTo(partnerOffer2);

        partnerOffer2.setId(partnerOffer1.getId());
        assertThat(partnerOffer1).isEqualTo(partnerOffer2);

        partnerOffer2 = getPartnerOfferSample2();
        assertThat(partnerOffer1).isNotEqualTo(partnerOffer2);
    }

    @Test
    void venueTest() {
        PartnerOffer partnerOffer = getPartnerOfferRandomSampleGenerator();
        PartnerVenue partnerVenueBack = getPartnerVenueRandomSampleGenerator();

        partnerOffer.setVenue(partnerVenueBack);
        assertThat(partnerOffer.getVenue()).isEqualTo(partnerVenueBack);

        partnerOffer.venue(null);
        assertThat(partnerOffer.getVenue()).isNull();
    }

    @Test
    void redemptionsTest() {
        PartnerOffer partnerOffer = getPartnerOfferRandomSampleGenerator();
        PartnerOfferRedemption partnerOfferRedemptionBack = getPartnerOfferRedemptionRandomSampleGenerator();

        partnerOffer.addRedemptions(partnerOfferRedemptionBack);
        assertThat(partnerOffer.getRedemptions()).containsOnly(partnerOfferRedemptionBack);
        assertThat(partnerOfferRedemptionBack.getOffer()).isEqualTo(partnerOffer);

        partnerOffer.removeRedemptions(partnerOfferRedemptionBack);
        assertThat(partnerOffer.getRedemptions()).doesNotContain(partnerOfferRedemptionBack);
        assertThat(partnerOfferRedemptionBack.getOffer()).isNull();

        partnerOffer.redemptions(new HashSet<>(Set.of(partnerOfferRedemptionBack)));
        assertThat(partnerOffer.getRedemptions()).containsOnly(partnerOfferRedemptionBack);
        assertThat(partnerOfferRedemptionBack.getOffer()).isEqualTo(partnerOffer);

        partnerOffer.setRedemptions(new HashSet<>());
        assertThat(partnerOffer.getRedemptions()).doesNotContain(partnerOfferRedemptionBack);
        assertThat(partnerOfferRedemptionBack.getOffer()).isNull();
    }
}
