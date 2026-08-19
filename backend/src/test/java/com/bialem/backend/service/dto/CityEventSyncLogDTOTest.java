package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventSyncLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventSyncLogDTO.class);
        CityEventSyncLogDTO cityEventSyncLogDTO1 = new CityEventSyncLogDTO();
        cityEventSyncLogDTO1.setId(1L);
        CityEventSyncLogDTO cityEventSyncLogDTO2 = new CityEventSyncLogDTO();
        assertThat(cityEventSyncLogDTO1).isNotEqualTo(cityEventSyncLogDTO2);
        cityEventSyncLogDTO2.setId(cityEventSyncLogDTO1.getId());
        assertThat(cityEventSyncLogDTO1).isEqualTo(cityEventSyncLogDTO2);
        cityEventSyncLogDTO2.setId(2L);
        assertThat(cityEventSyncLogDTO1).isNotEqualTo(cityEventSyncLogDTO2);
        cityEventSyncLogDTO1.setId(null);
        assertThat(cityEventSyncLogDTO1).isNotEqualTo(cityEventSyncLogDTO2);
    }
}
