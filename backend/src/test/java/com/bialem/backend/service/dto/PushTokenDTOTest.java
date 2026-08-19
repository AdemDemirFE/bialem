package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PushTokenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PushTokenDTO.class);
        PushTokenDTO pushTokenDTO1 = new PushTokenDTO();
        pushTokenDTO1.setId(1L);
        PushTokenDTO pushTokenDTO2 = new PushTokenDTO();
        assertThat(pushTokenDTO1).isNotEqualTo(pushTokenDTO2);
        pushTokenDTO2.setId(pushTokenDTO1.getId());
        assertThat(pushTokenDTO1).isEqualTo(pushTokenDTO2);
        pushTokenDTO2.setId(2L);
        assertThat(pushTokenDTO1).isNotEqualTo(pushTokenDTO2);
        pushTokenDTO1.setId(null);
        assertThat(pushTokenDTO1).isNotEqualTo(pushTokenDTO2);
    }
}
