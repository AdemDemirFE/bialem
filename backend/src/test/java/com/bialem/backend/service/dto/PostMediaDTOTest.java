package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PostMediaDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PostMediaDTO.class);
        PostMediaDTO postMediaDTO1 = new PostMediaDTO();
        postMediaDTO1.setId(1L);
        PostMediaDTO postMediaDTO2 = new PostMediaDTO();
        assertThat(postMediaDTO1).isNotEqualTo(postMediaDTO2);
        postMediaDTO2.setId(postMediaDTO1.getId());
        assertThat(postMediaDTO1).isEqualTo(postMediaDTO2);
        postMediaDTO2.setId(2L);
        assertThat(postMediaDTO1).isNotEqualTo(postMediaDTO2);
        postMediaDTO1.setId(null);
        assertThat(postMediaDTO1).isNotEqualTo(postMediaDTO2);
    }
}
