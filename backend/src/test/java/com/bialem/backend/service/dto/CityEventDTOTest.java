package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventDTO.class);
        CityEventDTO cityEventDTO1 = new CityEventDTO();
        cityEventDTO1.setId(1L);
        CityEventDTO cityEventDTO2 = new CityEventDTO();
        assertThat(cityEventDTO1).isNotEqualTo(cityEventDTO2);
        cityEventDTO2.setId(cityEventDTO1.getId());
        assertThat(cityEventDTO1).isEqualTo(cityEventDTO2);
        cityEventDTO2.setId(2L);
        assertThat(cityEventDTO1).isNotEqualTo(cityEventDTO2);
        cityEventDTO1.setId(null);
        assertThat(cityEventDTO1).isNotEqualTo(cityEventDTO2);
    }
}
