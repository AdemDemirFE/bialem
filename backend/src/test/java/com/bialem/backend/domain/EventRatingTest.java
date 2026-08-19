package com.bialem.backend.domain;

import static com.bialem.backend.domain.EventRatingTestSamples.*;
import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventRatingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventRating.class);
        EventRating eventRating1 = getEventRatingSample1();
        EventRating eventRating2 = new EventRating();
        assertThat(eventRating1).isNotEqualTo(eventRating2);

        eventRating2.setId(eventRating1.getId());
        assertThat(eventRating1).isEqualTo(eventRating2);

        eventRating2 = getEventRatingSample2();
        assertThat(eventRating1).isNotEqualTo(eventRating2);
    }

    @Test
    void eventTest() {
        EventRating eventRating = getEventRatingRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        eventRating.setEvent(eventBack);
        assertThat(eventRating.getEvent()).isEqualTo(eventBack);

        eventRating.event(null);
        assertThat(eventRating.getEvent()).isNull();
    }

    @Test
    void userTest() {
        EventRating eventRating = getEventRatingRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        eventRating.setUser(profileBack);
        assertThat(eventRating.getUser()).isEqualTo(profileBack);

        eventRating.user(null);
        assertThat(eventRating.getUser()).isNull();
    }
}
