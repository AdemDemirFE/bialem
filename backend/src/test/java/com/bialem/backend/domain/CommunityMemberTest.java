package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityMemberTestSamples.*;
import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommunityMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CommunityMember.class);
        CommunityMember communityMember1 = getCommunityMemberSample1();
        CommunityMember communityMember2 = new CommunityMember();
        assertThat(communityMember1).isNotEqualTo(communityMember2);

        communityMember2.setId(communityMember1.getId());
        assertThat(communityMember1).isEqualTo(communityMember2);

        communityMember2 = getCommunityMemberSample2();
        assertThat(communityMember1).isNotEqualTo(communityMember2);
    }

    @Test
    void communityTest() {
        CommunityMember communityMember = getCommunityMemberRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        communityMember.setCommunity(communityBack);
        assertThat(communityMember.getCommunity()).isEqualTo(communityBack);

        communityMember.community(null);
        assertThat(communityMember.getCommunity()).isNull();
    }

    @Test
    void userTest() {
        CommunityMember communityMember = getCommunityMemberRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        communityMember.setUser(profileBack);
        assertThat(communityMember.getUser()).isEqualTo(profileBack);

        communityMember.user(null);
        assertThat(communityMember.getUser()).isNull();
    }
}
