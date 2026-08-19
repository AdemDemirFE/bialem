package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EventMessageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EventMessageDTO.class);
        EventMessageDTO eventMessageDTO1 = new EventMessageDTO();
        eventMessageDTO1.setId(1L);
        EventMessageDTO eventMessageDTO2 = new EventMessageDTO();
        assertThat(eventMessageDTO1).isNotEqualTo(eventMessageDTO2);
        eventMessageDTO2.setId(eventMessageDTO1.getId());
        assertThat(eventMessageDTO1).isEqualTo(eventMessageDTO2);
        eventMessageDTO2.setId(2L);
        assertThat(eventMessageDTO1).isNotEqualTo(eventMessageDTO2);
        eventMessageDTO1.setId(null);
        assertThat(eventMessageDTO1).isNotEqualTo(eventMessageDTO2);
    }
}
