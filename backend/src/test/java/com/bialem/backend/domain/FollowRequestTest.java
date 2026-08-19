package com.bialem.backend.domain;

import static com.bialem.backend.domain.FollowRequestTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FollowRequest.class);
        FollowRequest followRequest1 = getFollowRequestSample1();
        FollowRequest followRequest2 = new FollowRequest();
        assertThat(followRequest1).isNotEqualTo(followRequest2);

        followRequest2.setId(followRequest1.getId());
        assertThat(followRequest1).isEqualTo(followRequest2);

        followRequest2 = getFollowRequestSample2();
        assertThat(followRequest1).isNotEqualTo(followRequest2);
    }

    @Test
    void requesterTest() {
        FollowRequest followRequest = getFollowRequestRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        followRequest.setRequester(profileBack);
        assertThat(followRequest.getRequester()).isEqualTo(profileBack);

        followRequest.requester(null);
        assertThat(followRequest.getRequester()).isNull();
    }

    @Test
    void targetUserTest() {
        FollowRequest followRequest = getFollowRequestRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        followRequest.setTargetUser(profileBack);
        assertThat(followRequest.getTargetUser()).isEqualTo(profileBack);

        followRequest.targetUser(null);
        assertThat(followRequest.getTargetUser()).isNull();
    }
}
