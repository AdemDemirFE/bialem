package com.bialem.backend.domain;

import static com.bialem.backend.domain.PartnerVenueStaffTestSamples.*;
import static com.bialem.backend.domain.PartnerVenueTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartnerVenueStaffTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerVenueStaff.class);
        PartnerVenueStaff partnerVenueStaff1 = getPartnerVenueStaffSample1();
        PartnerVenueStaff partnerVenueStaff2 = new PartnerVenueStaff();
        assertThat(partnerVenueStaff1).isNotEqualTo(partnerVenueStaff2);

        partnerVenueStaff2.setId(partnerVenueStaff1.getId());
        assertThat(partnerVenueStaff1).isEqualTo(partnerVenueStaff2);

        partnerVenueStaff2 = getPartnerVenueStaffSample2();
        assertThat(partnerVenueStaff1).isNotEqualTo(partnerVenueStaff2);
    }

    @Test
    void venueTest() {
        PartnerVenueStaff partnerVenueStaff = getPartnerVenueStaffRandomSampleGenerator();
        PartnerVenue partnerVenueBack = getPartnerVenueRandomSampleGenerator();

        partnerVenueStaff.setVenue(partnerVenueBack);
        assertThat(partnerVenueStaff.getVenue()).isEqualTo(partnerVenueBack);

        partnerVenueStaff.venue(null);
        assertThat(partnerVenueStaff.getVenue()).isNull();
    }

    @Test
    void userTest() {
        PartnerVenueStaff partnerVenueStaff = getPartnerVenueStaffRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        partnerVenueStaff.setUser(profileBack);
        assertThat(partnerVenueStaff.getUser()).isEqualTo(profileBack);

        partnerVenueStaff.user(null);
        assertThat(partnerVenueStaff.getUser()).isNull();
    }
}
