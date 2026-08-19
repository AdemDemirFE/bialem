package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommunityMemberDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommunityMemberDTO.class);
        CommunityMemberDTO communityMemberDTO1 = new CommunityMemberDTO();
        communityMemberDTO1.setId(1L);
        CommunityMemberDTO communityMemberDTO2 = new CommunityMemberDTO();
        assertThat(communityMemberDTO1).isNotEqualTo(communityMemberDTO2);
        communityMemberDTO2.setId(communityMemberDTO1.getId());
        assertThat(communityMemberDTO1).isEqualTo(communityMemberDTO2);
        communityMemberDTO2.setId(2L);
        assertThat(communityMemberDTO1).isNotEqualTo(communityMemberDTO2);
        communityMemberDTO1.setId(null);
        assertThat(communityMemberDTO1).isNotEqualTo(communityMemberDTO2);
    }
}
