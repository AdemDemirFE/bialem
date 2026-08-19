package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HonorBadgeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HonorBadgeDTO.class);
        HonorBadgeDTO honorBadgeDTO1 = new HonorBadgeDTO();
        honorBadgeDTO1.setId(1L);
        HonorBadgeDTO honorBadgeDTO2 = new HonorBadgeDTO();
        assertThat(honorBadgeDTO1).isNotEqualTo(honorBadgeDTO2);
        honorBadgeDTO2.setId(honorBadgeDTO1.getId());
        assertThat(honorBadgeDTO1).isEqualTo(honorBadgeDTO2);
        honorBadgeDTO2.setId(2L);
        assertThat(honorBadgeDTO1).isNotEqualTo(honorBadgeDTO2);
        honorBadgeDTO1.setId(null);
        assertThat(honorBadgeDTO1).isNotEqualTo(honorBadgeDTO2);
    }
}
