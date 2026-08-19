package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.EventMessageTestSamples.*;
import static com.bialem.backend.domain.EventParticipantTestSamples.*;
import static com.bialem.backend.domain.EventRatingTestSamples.*;
import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.PostTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Event.class);
        Event event1 = getEventSample1();
        Event event2 = new Event();
        assertThat(event1).isNotEqualTo(event2);

        event2.setId(event1.getId());
        assertThat(event1).isEqualTo(event2);

        event2 = getEventSample2();
        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void communityTest() {
        Event event = getEventRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        event.setCommunity(communityBack);
        assertThat(event.getCommunity()).isEqualTo(communityBack);

        event.community(null);
        assertThat(event.getCommunity()).isNull();
    }

    @Test
    void categoryTest() {
        Event event = getEventRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        event.setCategory(communityBack);
        assertThat(event.getCategory()).isEqualTo(communityBack);

        event.category(null);
        assertThat(event.getCategory()).isNull();
    }

    @Test
    void createdByTest() {
        Event event = getEventRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        event.setCreatedBy(profileBack);
        assertThat(event.getCreatedBy()).isEqualTo(profileBack);

        event.createdBy(null);
        assertThat(event.getCreatedBy()).isNull();
    }

    @Test
    void cancelledByTest() {
        Event event = getEventRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        event.setCancelledBy(profileBack);
        assertThat(event.getCancelledBy()).isEqualTo(profileBack);

        event.cancelledBy(null);
        assertThat(event.getCancelledBy()).isNull();
    }

    @Test
    void participantsTest() {
        Event event = getEventRandomSampleGenerator();
        EventParticipant eventParticipantBack = getEventParticipantRandomSampleGenerator();

        event.addParticipants(eventParticipantBack);
        assertThat(event.getParticipants()).containsOnly(eventParticipantBack);
        assertThat(eventParticipantBack.getEvent()).isEqualTo(event);

        event.removeParticipants(eventParticipantBack);
        assertThat(event.getParticipants()).doesNotContain(eventParticipantBack);
        assertThat(eventParticipantBack.getEvent()).isNull();

        event.participants(new HashSet<>(Set.of(eventParticipantBack)));
        assertThat(event.getParticipants()).containsOnly(eventParticipantBack);
        assertThat(eventParticipantBack.getEvent()).isEqualTo(event);

        event.setParticipants(new HashSet<>());
        assertThat(event.getParticipants()).doesNotContain(eventParticipantBack);
        assertThat(eventParticipantBack.getEvent()).isNull();
    }

    @Test
    void messagesTest() {
        Event event = getEventRandomSampleGenerator();
        EventMessage eventMessageBack = getEventMessageRandomSampleGenerator();

        event.addMessages(eventMessageBack);
        assertThat(event.getMessages()).containsOnly(eventMessageBack);
        assertThat(eventMessageBack.getEvent()).isEqualTo(event);

        event.removeMessages(eventMessageBack);
        assertThat(event.getMessages()).doesNotContain(eventMessageBack);
        assertThat(eventMessageBack.getEvent()).isNull();

        event.messages(new HashSet<>(Set.of(eventMessageBack)));
        assertThat(event.getMessages()).containsOnly(eventMessageBack);
        assertThat(eventMessageBack.getEvent()).isEqualTo(event);

        event.setMessages(new HashSet<>());
        assertThat(event.getMessages()).doesNotContain(eventMessageBack);
        assertThat(eventMessageBack.getEvent()).isNull();
    }

    @Test
    void ratingsTest() {
        Event event = getEventRandomSampleGenerator();
        EventRating eventRatingBack = getEventRatingRandomSampleGenerator();

        event.addRatings(eventRatingBack);
        assertThat(event.getRatings()).containsOnly(eventRatingBack);
        assertThat(eventRatingBack.getEvent()).isEqualTo(event);

        event.removeRatings(eventRatingBack);
        assertThat(event.getRatings()).doesNotContain(eventRatingBack);
        assertThat(eventRatingBack.getEvent()).isNull();

        event.ratings(new HashSet<>(Set.of(eventRatingBack)));
        assertThat(event.getRatings()).containsOnly(eventRatingBack);
        assertThat(eventRatingBack.getEvent()).isEqualTo(event);

        event.setRatings(new HashSet<>());
        assertThat(event.getRatings()).doesNotContain(eventRatingBack);
        assertThat(eventRatingBack.getEvent()).isNull();
    }

    @Test
    void postsTest() {
        Event event = getEventRandomSampleGenerator();
        Post postBack = getPostRandomSampleGenerator();

        event.addPosts(postBack);
        assertThat(event.getPosts()).containsOnly(postBack);
        assertThat(postBack.getEvent()).isEqualTo(event);

        event.removePosts(postBack);
        assertThat(event.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getEvent()).isNull();

        event.posts(new HashSet<>(Set.of(postBack)));
        assertThat(event.getPosts()).containsOnly(postBack);
        assertThat(postBack.getEvent()).isEqualTo(event);

        event.setPosts(new HashSet<>());
        assertThat(event.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getEvent()).isNull();
    }
}
