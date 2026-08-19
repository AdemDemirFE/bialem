package com.bialem.backend.domain;

import static com.bialem.backend.domain.EventMessageTestSamples.*;
import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventMessage.class);
        EventMessage eventMessage1 = getEventMessageSample1();
        EventMessage eventMessage2 = new EventMessage();
        assertThat(eventMessage1).isNotEqualTo(eventMessage2);

        eventMessage2.setId(eventMessage1.getId());
        assertThat(eventMessage1).isEqualTo(eventMessage2);

        eventMessage2 = getEventMessageSample2();
        assertThat(eventMessage1).isNotEqualTo(eventMessage2);
    }

    @Test
    void eventTest() {
        EventMessage eventMessage = getEventMessageRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        eventMessage.setEvent(eventBack);
        assertThat(eventMessage.getEvent()).isEqualTo(eventBack);

        eventMessage.event(null);
        assertThat(eventMessage.getEvent()).isNull();
    }

    @Test
    void authorTest() {
        EventMessage eventMessage = getEventMessageRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        eventMessage.setAuthor(profileBack);
        assertThat(eventMessage.getAuthor()).isEqualTo(profileBack);

        eventMessage.author(null);
        assertThat(eventMessage.getAuthor()).isNull();
    }
}
