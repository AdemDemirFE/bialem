package com.bialem.backend.domain;

import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.StoryTestSamples.*;
import static com.bialem.backend.domain.StoryViewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoryViewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoryView.class);
        StoryView storyView1 = getStoryViewSample1();
        StoryView storyView2 = new StoryView();
        assertThat(storyView1).isNotEqualTo(storyView2);

        storyView2.setId(storyView1.getId());
        assertThat(storyView1).isEqualTo(storyView2);

        storyView2 = getStoryViewSample2();
        assertThat(storyView1).isNotEqualTo(storyView2);
    }

    @Test
    void storyTest() {
        StoryView storyView = getStoryViewRandomSampleGenerator();
        Story storyBack = getStoryRandomSampleGenerator();

        storyView.setStory(storyBack);
        assertThat(storyView.getStory()).isEqualTo(storyBack);

        storyView.story(null);
        assertThat(storyView.getStory()).isNull();
    }

    @Test
    void viewerTest() {
        StoryView storyView = getStoryViewRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        storyView.setViewer(profileBack);
        assertThat(storyView.getViewer()).isEqualTo(profileBack);

        storyView.viewer(null);
        assertThat(storyView.getViewer()).isNull();
    }
}
