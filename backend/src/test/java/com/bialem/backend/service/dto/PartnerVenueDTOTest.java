package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartnerVenueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerVenueDTO.class);
        PartnerVenueDTO partnerVenueDTO1 = new PartnerVenueDTO();
        partnerVenueDTO1.setId(1L);
        PartnerVenueDTO partnerVenueDTO2 = new PartnerVenueDTO();
        assertThat(partnerVenueDTO1).isNotEqualTo(partnerVenueDTO2);
        partnerVenueDTO2.setId(partnerVenueDTO1.getId());
        assertThat(partnerVenueDTO1).isEqualTo(partnerVenueDTO2);
        partnerVenueDTO2.setId(2L);
        assertThat(partnerVenueDTO1).isNotEqualTo(partnerVenueDTO2);
        partnerVenueDTO1.setId(null);
        assertThat(partnerVenueDTO1).isNotEqualTo(partnerVenueDTO2);
    }
}
