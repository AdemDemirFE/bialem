package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommunityModeratorAssistantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommunityModeratorAssistantDTO.class);
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO1 = new CommunityModeratorAssistantDTO();
        communityModeratorAssistantDTO1.setId(1L);
        CommunityModeratorAssistantDTO communityModeratorAssistantDTO2 = new CommunityModeratorAssistantDTO();
        assertThat(communityModeratorAssistantDTO1).isNotEqualTo(communityModeratorAssistantDTO2);
        communityModeratorAssistantDTO2.setId(communityModeratorAssistantDTO1.getId());
        assertThat(communityModeratorAssistantDTO1).isEqualTo(communityModeratorAssistantDTO2);
        communityModeratorAssistantDTO2.setId(2L);
        assertThat(communityModeratorAssistantDTO1).isNotEqualTo(communityModeratorAssistantDTO2);
        communityModeratorAssistantDTO1.setId(null);
        assertThat(communityModeratorAssistantDTO1).isNotEqualTo(communityModeratorAssistantDTO2);
    }
}
