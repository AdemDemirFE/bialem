package com.bialem.backend.domain;

import static com.bialem.backend.domain.PartnerOfferTestSamples.*;
import static com.bialem.backend.domain.PartnerVenueStaffTestSamples.*;
import static com.bialem.backend.domain.PartnerVenueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PartnerVenueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerVenue.class);
        PartnerVenue partnerVenue1 = getPartnerVenueSample1();
        PartnerVenue partnerVenue2 = new PartnerVenue();
        assertThat(partnerVenue1).isNotEqualTo(partnerVenue2);

        partnerVenue2.setId(partnerVenue1.getId());
        assertThat(partnerVenue1).isEqualTo(partnerVenue2);

        partnerVenue2 = getPartnerVenueSample2();
        assertThat(partnerVenue1).isNotEqualTo(partnerVenue2);
    }

    @Test
    void offersTest() {
        PartnerVenue partnerVenue = getPartnerVenueRandomSampleGenerator();
        PartnerOffer partnerOfferBack = getPartnerOfferRandomSampleGenerator();

        partnerVenue.addOffers(partnerOfferBack);
        assertThat(partnerVenue.getOffers()).containsOnly(partnerOfferBack);
        assertThat(partnerOfferBack.getVenue()).isEqualTo(partnerVenue);

        partnerVenue.removeOffers(partnerOfferBack);
        assertThat(partnerVenue.getOffers()).doesNotContain(partnerOfferBack);
        assertThat(partnerOfferBack.getVenue()).isNull();

        partnerVenue.offers(new HashSet<>(Set.of(partnerOfferBack)));
        assertThat(partnerVenue.getOffers()).containsOnly(partnerOfferBack);
        assertThat(partnerOfferBack.getVenue()).isEqualTo(partnerVenue);

        partnerVenue.setOffers(new HashSet<>());
        assertThat(partnerVenue.getOffers()).doesNotContain(partnerOfferBack);
        assertThat(partnerOfferBack.getVenue()).isNull();
    }

    @Test
    void staffTest() {
        PartnerVenue partnerVenue = getPartnerVenueRandomSampleGenerator();
        PartnerVenueStaff partnerVenueStaffBack = getPartnerVenueStaffRandomSampleGenerator();

        partnerVenue.addStaff(partnerVenueStaffBack);
        assertThat(partnerVenue.getStaff()).containsOnly(partnerVenueStaffBack);
        assertThat(partnerVenueStaffBack.getVenue()).isEqualTo(partnerVenue);

        partnerVenue.removeStaff(partnerVenueStaffBack);
        assertThat(partnerVenue.getStaff()).doesNotContain(partnerVenueStaffBack);
        assertThat(partnerVenueStaffBack.getVenue()).isNull();

        partnerVenue.staff(new HashSet<>(Set.of(partnerVenueStaffBack)));
        assertThat(partnerVenue.getStaff()).containsOnly(partnerVenueStaffBack);
        assertThat(partnerVenueStaffBack.getVenue()).isEqualTo(partnerVenue);

        partnerVenue.setStaff(new HashSet<>());
        assertThat(partnerVenue.getStaff()).doesNotContain(partnerVenueStaffBack);
        assertThat(partnerVenueStaffBack.getVenue()).isNull();
    }
}
