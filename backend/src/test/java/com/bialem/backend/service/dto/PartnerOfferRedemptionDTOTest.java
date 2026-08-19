package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PartnerOfferRedemptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PartnerOfferRedemptionDTO.class);
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO1 = new PartnerOfferRedemptionDTO();
        partnerOfferRedemptionDTO1.setId(1L);
        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO2 = new PartnerOfferRedemptionDTO();
        assertThat(partnerOfferRedemptionDTO1).isNotEqualTo(partnerOfferRedemptionDTO2);
        partnerOfferRedemptionDTO2.setId(partnerOfferRedemptionDTO1.getId());
        assertThat(partnerOfferRedemptionDTO1).isEqualTo(partnerOfferRedemptionDTO2);
        partnerOfferRedemptionDTO2.setId(2L);
        assertThat(partnerOfferRedemptionDTO1).isNotEqualTo(partnerOfferRedemptionDTO2);
        partnerOfferRedemptionDTO1.setId(null);
        assertThat(partnerOfferRedemptionDTO1).isNotEqualTo(partnerOfferRedemptionDTO2);
    }
}
