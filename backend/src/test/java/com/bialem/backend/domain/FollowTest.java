package com.bialem.backend.domain;

import static com.bialem.backend.domain.FollowTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FollowTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Follow.class);
        Follow follow1 = getFollowSample1();
        Follow follow2 = new Follow();
        assertThat(follow1).isNotEqualTo(follow2);

        follow2.setId(follow1.getId());
        assertThat(follow1).isEqualTo(follow2);

        follow2 = getFollowSample2();
        assertThat(follow1).isNotEqualTo(follow2);
    }

    @Test
    void followerTest() {
        Follow follow = getFollowRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        follow.setFollower(profileBack);
        assertThat(follow.getFollower()).isEqualTo(profileBack);

        follow.follower(null);
        assertThat(follow.getFollower()).isNull();
    }

    @Test
    void followedTest() {
        Follow follow = getFollowRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        follow.setFollowed(profileBack);
        assertThat(follow.getFollowed()).isEqualTo(profileBack);

        follow.followed(null);
        assertThat(follow.getFollowed()).isNull();
    }
}
