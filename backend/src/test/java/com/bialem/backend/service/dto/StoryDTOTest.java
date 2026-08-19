package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StoryDTO.class);
        StoryDTO storyDTO1 = new StoryDTO();
        storyDTO1.setId(1L);
        StoryDTO storyDTO2 = new StoryDTO();
        assertThat(storyDTO1).isNotEqualTo(storyDTO2);
        storyDTO2.setId(storyDTO1.getId());
        assertThat(storyDTO1).isEqualTo(storyDTO2);
        storyDTO2.setId(2L);
        assertThat(storyDTO1).isNotEqualTo(storyDTO2);
        storyDTO1.setId(null);
        assertThat(storyDTO1).isNotEqualTo(storyDTO2);
    }
}
