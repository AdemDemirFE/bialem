package com.bialem.backend.domain;

import static com.bialem.backend.domain.HonorBadgeTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.UserHonorBadgeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserHonorBadgeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserHonorBadge.class);
        UserHonorBadge userHonorBadge1 = getUserHonorBadgeSample1();
        UserHonorBadge userHonorBadge2 = new UserHonorBadge();
        assertThat(userHonorBadge1).isNotEqualTo(userHonorBadge2);

        userHonorBadge2.setId(userHonorBadge1.getId());
        assertThat(userHonorBadge1).isEqualTo(userHonorBadge2);

        userHonorBadge2 = getUserHonorBadgeSample2();
        assertThat(userHonorBadge1).isNotEqualTo(userHonorBadge2);
    }

    @Test
    void userTest() {
        UserHonorBadge userHonorBadge = getUserHonorBadgeRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        userHonorBadge.setUser(profileBack);
        assertThat(userHonorBadge.getUser()).isEqualTo(profileBack);

        userHonorBadge.user(null);
        assertThat(userHonorBadge.getUser()).isNull();
    }

    @Test
    void badgeTest() {
        UserHonorBadge userHonorBadge = getUserHonorBadgeRandomSampleGenerator();
        HonorBadge honorBadgeBack = getHonorBadgeRandomSampleGenerator();

        userHonorBadge.setBadge(honorBadgeBack);
        assertThat(userHonorBadge.getBadge()).isEqualTo(honorBadgeBack);

        userHonorBadge.badge(null);
        assertThat(userHonorBadge.getBadge()).isNull();
    }

    @Test
    void awardedByTest() {
        UserHonorBadge userHonorBadge = getUserHonorBadgeRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        userHonorBadge.setAwardedBy(profileBack);
        assertThat(userHonorBadge.getAwardedBy()).isEqualTo(profileBack);

        userHonorBadge.awardedBy(null);
        assertThat(userHonorBadge.getAwardedBy()).isNull();
    }
}
