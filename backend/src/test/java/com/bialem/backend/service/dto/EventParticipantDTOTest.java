package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventParticipantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventParticipantDTO.class);
        EventParticipantDTO eventParticipantDTO1 = new EventParticipantDTO();
        eventParticipantDTO1.setId(1L);
        EventParticipantDTO eventParticipantDTO2 = new EventParticipantDTO();
        assertThat(eventParticipantDTO1).isNotEqualTo(eventParticipantDTO2);
        eventParticipantDTO2.setId(eventParticipantDTO1.getId());
        assertThat(eventParticipantDTO1).isEqualTo(eventParticipantDTO2);
        eventParticipantDTO2.setId(2L);
        assertThat(eventParticipantDTO1).isNotEqualTo(eventParticipantDTO2);
        eventParticipantDTO1.setId(null);
        assertThat(eventParticipantDTO1).isNotEqualTo(eventParticipantDTO2);
    }
}
