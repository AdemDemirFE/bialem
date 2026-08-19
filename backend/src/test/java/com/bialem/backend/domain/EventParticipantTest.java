package com.bialem.backend.domain;

import static com.bialem.backend.domain.EventParticipantTestSamples.*;
import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventParticipantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventParticipant.class);
        EventParticipant eventParticipant1 = getEventParticipantSample1();
        EventParticipant eventParticipant2 = new EventParticipant();
        assertThat(eventParticipant1).isNotEqualTo(eventParticipant2);

        eventParticipant2.setId(eventParticipant1.getId());
        assertThat(eventParticipant1).isEqualTo(eventParticipant2);

        eventParticipant2 = getEventParticipantSample2();
        assertThat(eventParticipant1).isNotEqualTo(eventParticipant2);
    }

    @Test
    void eventTest() {
        EventParticipant eventParticipant = getEventParticipantRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        eventParticipant.setEvent(eventBack);
        assertThat(eventParticipant.getEvent()).isEqualTo(eventBack);

        eventParticipant.event(null);
        assertThat(eventParticipant.getEvent()).isNull();
    }

    @Test
    void userTest() {
        EventParticipant eventParticipant = getEventParticipantRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        eventParticipant.setUser(profileBack);
        assertThat(eventParticipant.getUser()).isEqualTo(profileBack);

        eventParticipant.user(null);
        assertThat(eventParticipant.getUser()).isNull();
    }
}
