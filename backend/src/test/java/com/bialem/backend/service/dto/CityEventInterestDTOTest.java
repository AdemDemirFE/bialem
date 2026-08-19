package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventInterestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventInterestDTO.class);
        CityEventInterestDTO cityEventInterestDTO1 = new CityEventInterestDTO();
        cityEventInterestDTO1.setId(1L);
        CityEventInterestDTO cityEventInterestDTO2 = new CityEventInterestDTO();
        assertThat(cityEventInterestDTO1).isNotEqualTo(cityEventInterestDTO2);
        cityEventInterestDTO2.setId(cityEventInterestDTO1.getId());
        assertThat(cityEventInterestDTO1).isEqualTo(cityEventInterestDTO2);
        cityEventInterestDTO2.setId(2L);
        assertThat(cityEventInterestDTO1).isNotEqualTo(cityEventInterestDTO2);
        cityEventInterestDTO1.setId(null);
        assertThat(cityEventInterestDTO1).isNotEqualTo(cityEventInterestDTO2);
    }
}
