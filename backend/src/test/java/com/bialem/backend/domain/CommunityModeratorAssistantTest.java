package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityModeratorAssistantTestSamples.*;
import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommunityModeratorAssistantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommunityModeratorAssistant.class);
        CommunityModeratorAssistant communityModeratorAssistant1 = getCommunityModeratorAssistantSample1();
        CommunityModeratorAssistant communityModeratorAssistant2 = new CommunityModeratorAssistant();
        assertThat(communityModeratorAssistant1).isNotEqualTo(communityModeratorAssistant2);

        communityModeratorAssistant2.setId(communityModeratorAssistant1.getId());
        assertThat(communityModeratorAssistant1).isEqualTo(communityModeratorAssistant2);

        communityModeratorAssistant2 = getCommunityModeratorAssistantSample2();
        assertThat(communityModeratorAssistant1).isNotEqualTo(communityModeratorAssistant2);
    }

    @Test
    void communityTest() {
        CommunityModeratorAssistant communityModeratorAssistant = getCommunityModeratorAssistantRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        communityModeratorAssistant.setCommunity(communityBack);
        assertThat(communityModeratorAssistant.getCommunity()).isEqualTo(communityBack);

        communityModeratorAssistant.community(null);
        assertThat(communityModeratorAssistant.getCommunity()).isNull();
    }

    @Test
    void userTest() {
        CommunityModeratorAssistant communityModeratorAssistant = getCommunityModeratorAssistantRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        communityModeratorAssistant.setUser(profileBack);
        assertThat(communityModeratorAssistant.getUser()).isEqualTo(profileBack);

        communityModeratorAssistant.user(null);
        assertThat(communityModeratorAssistant.getUser()).isNull();
    }
}
