package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartnerOfferDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerOfferDTO.class);
        PartnerOfferDTO partnerOfferDTO1 = new PartnerOfferDTO();
        partnerOfferDTO1.setId(1L);
        PartnerOfferDTO partnerOfferDTO2 = new PartnerOfferDTO();
        assertThat(partnerOfferDTO1).isNotEqualTo(partnerOfferDTO2);
        partnerOfferDTO2.setId(partnerOfferDTO1.getId());
        assertThat(partnerOfferDTO1).isEqualTo(partnerOfferDTO2);
        partnerOfferDTO2.setId(2L);
        assertThat(partnerOfferDTO1).isNotEqualTo(partnerOfferDTO2);
        partnerOfferDTO1.setId(null);
        assertThat(partnerOfferDTO1).isNotEqualTo(partnerOfferDTO2);
    }
}
