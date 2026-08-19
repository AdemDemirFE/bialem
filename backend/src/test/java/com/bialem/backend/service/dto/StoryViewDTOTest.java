package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoryViewDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoryViewDTO.class);
        StoryViewDTO storyViewDTO1 = new StoryViewDTO();
        storyViewDTO1.setId(1L);
        StoryViewDTO storyViewDTO2 = new StoryViewDTO();
        assertThat(storyViewDTO1).isNotEqualTo(storyViewDTO2);
        storyViewDTO2.setId(storyViewDTO1.getId());
        assertThat(storyViewDTO1).isEqualTo(storyViewDTO2);
        storyViewDTO2.setId(2L);
        assertThat(storyViewDTO1).isNotEqualTo(storyViewDTO2);
        storyViewDTO1.setId(null);
        assertThat(storyViewDTO1).isNotEqualTo(storyViewDTO2);
    }
}
