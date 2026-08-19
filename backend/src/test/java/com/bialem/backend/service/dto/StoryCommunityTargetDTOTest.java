package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoryCommunityTargetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoryCommunityTargetDTO.class);
        StoryCommunityTargetDTO storyCommunityTargetDTO1 = new StoryCommunityTargetDTO();
        storyCommunityTargetDTO1.setId(1L);
        StoryCommunityTargetDTO storyCommunityTargetDTO2 = new StoryCommunityTargetDTO();
        assertThat(storyCommunityTargetDTO1).isNotEqualTo(storyCommunityTargetDTO2);
        storyCommunityTargetDTO2.setId(storyCommunityTargetDTO1.getId());
        assertThat(storyCommunityTargetDTO1).isEqualTo(storyCommunityTargetDTO2);
        storyCommunityTargetDTO2.setId(2L);
        assertThat(storyCommunityTargetDTO1).isNotEqualTo(storyCommunityTargetDTO2);
        storyCommunityTargetDTO1.setId(null);
        assertThat(storyCommunityTargetDTO1).isNotEqualTo(storyCommunityTargetDTO2);
    }
}
