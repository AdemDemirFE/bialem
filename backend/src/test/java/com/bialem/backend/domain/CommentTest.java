package com.bialem.backend.domain;

import static com.bialem.backend.domain.CommentTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = getCommentSample1();
        Comment comment2 = new Comment();
        assertThat(comment1).isNotEqualTo(comment2);

        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);

        comment2 = getCommentSample2();
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void authorTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        comment.setAuthor(profileBack);
        assertThat(comment.getAuthor()).isEqualTo(profileBack);

        comment.author(null);
        assertThat(comment.getAuthor()).isNull();
    }
}
