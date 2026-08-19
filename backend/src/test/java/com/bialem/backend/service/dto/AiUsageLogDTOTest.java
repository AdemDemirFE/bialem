package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiUsageLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiUsageLogDTO.class);
        AiUsageLogDTO aiUsageLogDTO1 = new AiUsageLogDTO();
        aiUsageLogDTO1.setId(1L);
        AiUsageLogDTO aiUsageLogDTO2 = new AiUsageLogDTO();
        assertThat(aiUsageLogDTO1).isNotEqualTo(aiUsageLogDTO2);
        aiUsageLogDTO2.setId(aiUsageLogDTO1.getId());
        assertThat(aiUsageLogDTO1).isEqualTo(aiUsageLogDTO2);
        aiUsageLogDTO2.setId(2L);
        assertThat(aiUsageLogDTO1).isNotEqualTo(aiUsageLogDTO2);
        aiUsageLogDTO1.setId(null);
        assertThat(aiUsageLogDTO1).isNotEqualTo(aiUsageLogDTO2);
    }
}
