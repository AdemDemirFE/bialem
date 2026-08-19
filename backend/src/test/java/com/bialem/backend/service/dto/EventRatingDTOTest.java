package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventRatingDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventRatingDTO.class);
        EventRatingDTO eventRatingDTO1 = new EventRatingDTO();
        eventRatingDTO1.setId(1L);
        EventRatingDTO eventRatingDTO2 = new EventRatingDTO();
        assertThat(eventRatingDTO1).isNotEqualTo(eventRatingDTO2);
        eventRatingDTO2.setId(eventRatingDTO1.getId());
        assertThat(eventRatingDTO1).isEqualTo(eventRatingDTO2);
        eventRatingDTO2.setId(2L);
        assertThat(eventRatingDTO1).isNotEqualTo(eventRatingDTO2);
        eventRatingDTO1.setId(null);
        assertThat(eventRatingDTO1).isNotEqualTo(eventRatingDTO2);
    }
}
