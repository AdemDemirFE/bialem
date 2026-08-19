package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserHonorBadgeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserHonorBadgeDTO.class);
        UserHonorBadgeDTO userHonorBadgeDTO1 = new UserHonorBadgeDTO();
        userHonorBadgeDTO1.setId(1L);
        UserHonorBadgeDTO userHonorBadgeDTO2 = new UserHonorBadgeDTO();
        assertThat(userHonorBadgeDTO1).isNotEqualTo(userHonorBadgeDTO2);
        userHonorBadgeDTO2.setId(userHonorBadgeDTO1.getId());
        assertThat(userHonorBadgeDTO1).isEqualTo(userHonorBadgeDTO2);
        userHonorBadgeDTO2.setId(2L);
        assertThat(userHonorBadgeDTO1).isNotEqualTo(userHonorBadgeDTO2);
        userHonorBadgeDTO1.setId(null);
        assertThat(userHonorBadgeDTO1).isNotEqualTo(userHonorBadgeDTO2);
    }
}
