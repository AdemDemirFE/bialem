package com.bialem.backend.domain;

import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.StoryCommunityTargetTestSamples.*;
import static com.bialem.backend.domain.StoryTestSamples.*;
import static com.bialem.backend.domain.StoryViewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class StoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Story.class);
        Story story1 = getStorySample1();
        Story story2 = new Story();
        assertThat(story1).isNotEqualTo(story2);

        story2.setId(story1.getId());
        assertThat(story1).isEqualTo(story2);

        story2 = getStorySample2();
        assertThat(story1).isNotEqualTo(story2);
    }

    @Test
    void authorTest() {
        Story story = getStoryRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        story.setAuthor(profileBack);
        assertThat(story.getAuthor()).isEqualTo(profileBack);

        story.author(null);
        assertThat(story.getAuthor()).isNull();
    }

    @Test
    void viewsTest() {
        Story story = getStoryRandomSampleGenerator();
        StoryView storyViewBack = getStoryViewRandomSampleGenerator();

        story.addViews(storyViewBack);
        assertThat(story.getViews()).containsOnly(storyViewBack);
        assertThat(storyViewBack.getStory()).isEqualTo(story);

        story.removeViews(storyViewBack);
        assertThat(story.getViews()).doesNotContain(storyViewBack);
        assertThat(storyViewBack.getStory()).isNull();

        story.views(new HashSet<>(Set.of(storyViewBack)));
        assertThat(story.getViews()).containsOnly(storyViewBack);
        assertThat(storyViewBack.getStory()).isEqualTo(story);

        story.setViews(new HashSet<>());
        assertThat(story.getViews()).doesNotContain(storyViewBack);
        assertThat(storyViewBack.getStory()).isNull();
    }

    @Test
    void communityTargetsTest() {
        Story story = getStoryRandomSampleGenerator();
        StoryCommunityTarget storyCommunityTargetBack = getStoryCommunityTargetRandomSampleGenerator();

        story.addCommunityTargets(storyCommunityTargetBack);
        assertThat(story.getCommunityTargets()).containsOnly(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getStory()).isEqualTo(story);

        story.removeCommunityTargets(storyCommunityTargetBack);
        assertThat(story.getCommunityTargets()).doesNotContain(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getStory()).isNull();

        story.communityTargets(new HashSet<>(Set.of(storyCommunityTargetBack)));
        assertThat(story.getCommunityTargets()).containsOnly(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getStory()).isEqualTo(story);

        story.setCommunityTargets(new HashSet<>());
        assertThat(story.getCommunityTargets()).doesNotContain(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getStory()).isNull();
    }
}
