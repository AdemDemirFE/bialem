package com.bialem.backend.domain;

import static com.bialem.backend.domain.CityEventSyncLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventSyncLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventSyncLog.class);
        CityEventSyncLog cityEventSyncLog1 = getCityEventSyncLogSample1();
        CityEventSyncLog cityEventSyncLog2 = new CityEventSyncLog();
        assertThat(cityEventSyncLog1).isNotEqualTo(cityEventSyncLog2);

        cityEventSyncLog2.setId(cityEventSyncLog1.getId());
        assertThat(cityEventSyncLog1).isEqualTo(cityEventSyncLog2);

        cityEventSyncLog2 = getCityEventSyncLogSample2();
        assertThat(cityEventSyncLog1).isNotEqualTo(cityEventSyncLog2);
    }
}
