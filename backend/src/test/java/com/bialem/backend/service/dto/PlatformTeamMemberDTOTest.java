package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlatformTeamMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlatformTeamMemberDTO.class);
        PlatformTeamMemberDTO platformTeamMemberDTO1 = new PlatformTeamMemberDTO();
        platformTeamMemberDTO1.setId(1L);
        PlatformTeamMemberDTO platformTeamMemberDTO2 = new PlatformTeamMemberDTO();
        assertThat(platformTeamMemberDTO1).isNotEqualTo(platformTeamMemberDTO2);
        platformTeamMemberDTO2.setId(platformTeamMemberDTO1.getId());
        assertThat(platformTeamMemberDTO1).isEqualTo(platformTeamMemberDTO2);
        platformTeamMemberDTO2.setId(2L);
        assertThat(platformTeamMemberDTO1).isNotEqualTo(platformTeamMemberDTO2);
        platformTeamMemberDTO1.setId(null);
        assertThat(platformTeamMemberDTO1).isNotEqualTo(platformTeamMemberDTO2);
    }
}
