package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommunityTestSamples.*;
import static com.bialem.backend.domain.EventTestSamples.*;
import static com.bialem.backend.domain.PostMediaTestSamples.*;
import static com.bialem.backend.domain.PostTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PostTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Post.class);
        Post post1 = getPostSample1();
        Post post2 = new Post();
        assertThat(post1).isNotEqualTo(post2);

        post2.setId(post1.getId());
        assertThat(post1).isEqualTo(post2);

        post2 = getPostSample2();
        assertThat(post1).isNotEqualTo(post2);
    }

    @Test
    void communityTest() {
        Post post = getPostRandomSampleGenerator();
        Community communityBack = getCommunityRandomSampleGenerator();

        post.setCommunity(communityBack);
        assertThat(post.getCommunity()).isEqualTo(communityBack);

        post.community(null);
        assertThat(post.getCommunity()).isNull();
    }

    @Test
    void eventTest() {
        Post post = getPostRandomSampleGenerator();
        Event eventBack = getEventRandomSampleGenerator();

        post.setEvent(eventBack);
        assertThat(post.getEvent()).isEqualTo(eventBack);

        post.event(null);
        assertThat(post.getEvent()).isNull();
    }

    @Test
    void authorTest() {
        Post post = getPostRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        post.setAuthor(profileBack);
        assertThat(post.getAuthor()).isEqualTo(profileBack);

        post.author(null);
        assertThat(post.getAuthor()).isNull();
    }

    @Test
    void mediaTest() {
        Post post = getPostRandomSampleGenerator();
        PostMedia postMediaBack = getPostMediaRandomSampleGenerator();

        post.addMedia(postMediaBack);
        assertThat(post.getMedia()).containsOnly(postMediaBack);
        assertThat(postMediaBack.getPost()).isEqualTo(post);

        post.removeMedia(postMediaBack);
        assertThat(post.getMedia()).doesNotContain(postMediaBack);
        assertThat(postMediaBack.getPost()).isNull();

        post.media(new HashSet<>(Set.of(postMediaBack)));
        assertThat(post.getMedia()).containsOnly(postMediaBack);
        assertThat(postMediaBack.getPost()).isEqualTo(post);

        post.setMedia(new HashSet<>());
        assertThat(post.getMedia()).doesNotContain(postMediaBack);
        assertThat(postMediaBack.getPost()).isNull();
    }
}
