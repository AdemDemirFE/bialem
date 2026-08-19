package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.StoryCommunityTargetTestSamples.*;
import static com.bialem.backend.domain.StoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoryCommunityTargetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoryCommunityTarget.class);
        StoryCommunityTarget storyCommunityTarget1 = getStoryCommunityTargetSample1();
        StoryCommunityTarget storyCommunityTarget2 = new StoryCommunityTarget();
        assertThat(storyCommunityTarget1).isNotEqualTo(storyCommunityTarget2);

        storyCommunityTarget2.setId(storyCommunityTarget1.getId());
        assertThat(storyCommunityTarget1).isEqualTo(storyCommunityTarget2);

        storyCommunityTarget2 = getStoryCommunityTargetSample2();
        assertThat(storyCommunityTarget1).isNotEqualTo(storyCommunityTarget2);
    }

    @Test
    void storyTest() {
        StoryCommunityTarget storyCommunityTarget = getStoryCommunityTargetRandomSampleGenerator();
        Story storyBack = getStoryRandomSampleGenerator();

        storyCommunityTarget.setStory(storyBack);
        assertThat(storyCommunityTarget.getStory()).isEqualTo(storyBack);

        storyCommunityTarget.story(null);
        assertThat(storyCommunityTarget.getStory()).isNull();
    }

    @Test
    void communityTest() {
        StoryCommunityTarget storyCommunityTarget = getStoryCommunityTargetRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        storyCommunityTarget.setCommunity(communityBack);
        assertThat(storyCommunityTarget.getCommunity()).isEqualTo(communityBack);

        storyCommunityTarget.community(null);
        assertThat(storyCommunityTarget.getCommunity()).isNull();
    }
}
