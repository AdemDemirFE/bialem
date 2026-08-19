package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartnerVenueStaffDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerVenueStaffDTO.class);
        PartnerVenueStaffDTO partnerVenueStaffDTO1 = new PartnerVenueStaffDTO();
        partnerVenueStaffDTO1.setId(1L);
        PartnerVenueStaffDTO partnerVenueStaffDTO2 = new PartnerVenueStaffDTO();
        assertThat(partnerVenueStaffDTO1).isNotEqualTo(partnerVenueStaffDTO2);
        partnerVenueStaffDTO2.setId(partnerVenueStaffDTO1.getId());
        assertThat(partnerVenueStaffDTO1).isEqualTo(partnerVenueStaffDTO2);
        partnerVenueStaffDTO2.setId(2L);
        assertThat(partnerVenueStaffDTO1).isNotEqualTo(partnerVenueStaffDTO2);
        partnerVenueStaffDTO1.setId(null);
        assertThat(partnerVenueStaffDTO1).isNotEqualTo(partnerVenueStaffDTO2);
    }
}
