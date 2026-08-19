package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityMemberTestSamples.*;
import static com.bialem.backend.domain.CommunityModeratorAssistantTestSamples.*;
import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.PostTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.StoryCommunityTargetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CommunityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Community.class);
        Community community1 = getCommunitySample1();
        Community community2 = new Community();
        assertThat(community1).isNotEqualTo(community2);

        community2.setId(community1.getId());
        assertThat(community1).isEqualTo(community2);

        community2 = getCommunitySample2();
        assertThat(community1).isNotEqualTo(community2);
    }

    @Test
    void parentTest() {
        Community community = getCommunityRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        community.setParent(communityBack);
        assertThat(community.getParent()).isEqualTo(communityBack);

        community.parent(null);
        assertThat(community.getParent()).isNull();
    }

    @Test
    void categoryHubTest() {
        Community community = getCommunityRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        community.setCategoryHub(communityBack);
        assertThat(community.getCategoryHub()).isEqualTo(communityBack);

        community.categoryHub(null);
        assertThat(community.getCategoryHub()).isNull();
    }

    @Test
    void createdByTest() {
        Community community = getCommunityRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        community.setCreatedBy(profileBack);
        assertThat(community.getCreatedBy()).isEqualTo(profileBack);

        community.createdBy(null);
        assertThat(community.getCreatedBy()).isNull();
    }

    @Test
    void leadModeratorTest() {
        Community community = getCommunityRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        community.setLeadModerator(profileBack);
        assertThat(community.getLeadModerator()).isEqualTo(profileBack);

        community.leadModerator(null);
        assertThat(community.getLeadModerator()).isNull();
    }

    @Test
    void childrenTest() {
        Community community = getCommunityRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        community.addChildren(communityBack);
        assertThat(community.getChildren()).containsOnly(communityBack);
        assertThat(communityBack.getParent()).isEqualTo(community);

        community.removeChildren(communityBack);
        assertThat(community.getChildren()).doesNotContain(communityBack);
        assertThat(communityBack.getParent()).isNull();

        community.children(new HashSet<>(Set.of(communityBack)));
        assertThat(community.getChildren()).containsOnly(communityBack);
        assertThat(communityBack.getParent()).isEqualTo(community);

        community.setChildren(new HashSet<>());
        assertThat(community.getChildren()).doesNotContain(communityBack);
        assertThat(communityBack.getParent()).isNull();
    }

    @Test
    void categorizedGroupsTest() {
        Community community = getCommunityRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        community.addCategorizedGroups(communityBack);
        assertThat(community.getCategorizedGroups()).containsOnly(communityBack);
        assertThat(communityBack.getCategoryHub()).isEqualTo(community);

        community.removeCategorizedGroups(communityBack);
        assertThat(community.getCategorizedGroups()).doesNotContain(communityBack);
        assertThat(communityBack.getCategoryHub()).isNull();

        community.categorizedGroups(new HashSet<>(Set.of(communityBack)));
        assertThat(community.getCategorizedGroups()).containsOnly(communityBack);
        assertThat(communityBack.getCategoryHub()).isEqualTo(community);

        community.setCategorizedGroups(new HashSet<>());
        assertThat(community.getCategorizedGroups()).doesNotContain(communityBack);
        assertThat(communityBack.getCategoryHub()).isNull();
    }

    @Test
    void membersTest() {
        Community community = getCommunityRandomSampleGenerator();
        CommunityMember communityMemberBack = getCommunityMemberRandomSampleGenerator();

        community.addMembers(communityMemberBack);
        assertThat(community.getMembers()).containsOnly(communityMemberBack);
        assertThat(communityMemberBack.getCommunity()).isEqualTo(community);

        community.removeMembers(communityMemberBack);
        assertThat(community.getMembers()).doesNotContain(communityMemberBack);
        assertThat(communityMemberBack.getCommunity()).isNull();

        community.members(new HashSet<>(Set.of(communityMemberBack)));
        assertThat(community.getMembers()).containsOnly(communityMemberBack);
        assertThat(communityMemberBack.getCommunity()).isEqualTo(community);

        community.setMembers(new HashSet<>());
        assertThat(community.getMembers()).doesNotContain(communityMemberBack);
        assertThat(communityMemberBack.getCommunity()).isNull();
    }

    @Test
    void assistantsTest() {
        Community community = getCommunityRandomSampleGenerator();
        CommunityModeratorAssistant communityModeratorAssistantBack = getCommunityModeratorAssistantRandomSampleGenerator();

        community.addAssistants(communityModeratorAssistantBack);
        assertThat(community.getAssistants()).containsOnly(communityModeratorAssistantBack);
        assertThat(communityModeratorAssistantBack.getCommunity()).isEqualTo(community);

        community.removeAssistants(communityModeratorAssistantBack);
        assertThat(community.getAssistants()).doesNotContain(communityModeratorAssistantBack);
        assertThat(communityModeratorAssistantBack.getCommunity()).isNull();

        community.assistants(new HashSet<>(Set.of(communityModeratorAssistantBack)));
        assertThat(community.getAssistants()).containsOnly(communityModeratorAssistantBack);
        assertThat(communityModeratorAssistantBack.getCommunity()).isEqualTo(community);

        community.setAssistants(new HashSet<>());
        assertThat(community.getAssistants()).doesNotContain(communityModeratorAssistantBack);
        assertThat(communityModeratorAssistantBack.getCommunity()).isNull();
    }

    @Test
    void eventsTest() {
        Community community = getCommunityRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        community.addEvents(eventBack);
        assertThat(community.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getCommunity()).isEqualTo(community);

        community.removeEvents(eventBack);
        assertThat(community.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getCommunity()).isNull();

        community.events(new HashSet<>(Set.of(eventBack)));
        assertThat(community.getEvents()).containsOnly(eventBack);
        assertThat(eventBack.getCommunity()).isEqualTo(community);

        community.setEvents(new HashSet<>());
        assertThat(community.getEvents()).doesNotContain(eventBack);
        assertThat(eventBack.getCommunity()).isNull();
    }

    @Test
    void postsTest() {
        Community community = getCommunityRandomSampleGenerator();
        Post postBack = getPostRandomSampleGenerator();

        community.addPosts(postBack);
        assertThat(community.getPosts()).containsOnly(postBack);
        assertThat(postBack.getCommunity()).isEqualTo(community);

        community.removePosts(postBack);
        assertThat(community.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getCommunity()).isNull();

        community.posts(new HashSet<>(Set.of(postBack)));
        assertThat(community.getPosts()).containsOnly(postBack);
        assertThat(postBack.getCommunity()).isEqualTo(community);

        community.setPosts(new HashSet<>());
        assertThat(community.getPosts()).doesNotContain(postBack);
        assertThat(postBack.getCommunity()).isNull();
    }

    @Test
    void storyTargetsTest() {
        Community community = getCommunityRandomSampleGenerator();
        StoryCommunityTarget storyCommunityTargetBack = getStoryCommunityTargetRandomSampleGenerator();

        community.addStoryTargets(storyCommunityTargetBack);
        assertThat(community.getStoryTargets()).containsOnly(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getCommunity()).isEqualTo(community);

        community.removeStoryTargets(storyCommunityTargetBack);
        assertThat(community.getStoryTargets()).doesNotContain(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getCommunity()).isNull();

        community.storyTargets(new HashSet<>(Set.of(storyCommunityTargetBack)));
        assertThat(community.getStoryTargets()).containsOnly(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getCommunity()).isEqualTo(community);

        community.setStoryTargets(new HashSet<>());
        assertThat(community.getStoryTargets()).doesNotContain(storyCommunityTargetBack);
        assertThat(storyCommunityTargetBack.getCommunity()).isNull();
    }
}
