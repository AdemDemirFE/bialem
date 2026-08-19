package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowRequestDTO.class);
        FollowRequestDTO followRequestDTO1 = new FollowRequestDTO();
        followRequestDTO1.setId(1L);
        FollowRequestDTO followRequestDTO2 = new FollowRequestDTO();
        assertThat(followRequestDTO1).isNotEqualTo(followRequestDTO2);
        followRequestDTO2.setId(followRequestDTO1.getId());
        assertThat(followRequestDTO1).isEqualTo(followRequestDTO2);
        followRequestDTO2.setId(2L);
        assertThat(followRequestDTO1).isNotEqualTo(followRequestDTO2);
        followRequestDTO1.setId(null);
        assertThat(followRequestDTO1).isNotEqualTo(followRequestDTO2);
    }
}
