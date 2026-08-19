package com.bialem.backend.domain;

import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.UserReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserReview.class);
        UserReview userReview1 = getUserReviewSample1();
        UserReview userReview2 = new UserReview();
        assertThat(userReview1).isNotEqualTo(userReview2);

        userReview2.setId(userReview1.getId());
        assertThat(userReview1).isEqualTo(userReview2);

        userReview2 = getUserReviewSample2();
        assertThat(userReview1).isNotEqualTo(userReview2);
    }

    @Test
    void reviewerTest() {
        UserReview userReview = getUserReviewRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        userReview.setReviewer(profileBack);
        assertThat(userReview.getReviewer()).isEqualTo(profileBack);

        userReview.reviewer(null);
        assertThat(userReview.getReviewer()).isNull();
    }

    @Test
    void reviewedUserTest() {
        UserReview userReview = getUserReviewRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        userReview.setReviewedUser(profileBack);
        assertThat(userReview.getReviewedUser()).isEqualTo(profileBack);

        userReview.reviewedUser(null);
        assertThat(userReview.getReviewedUser()).isNull();
    }

    @Test
    void eventTest() {
        UserReview userReview = getUserReviewRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        userReview.setEvent(eventBack);
        assertThat(userReview.getEvent()).isEqualTo(eventBack);

        userReview.event(null);
        assertThat(userReview.getEvent()).isNull();
    }
}
