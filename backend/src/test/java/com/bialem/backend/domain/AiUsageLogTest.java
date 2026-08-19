package com.bialem.backend.domain;

import static com.bialem.backend.domain.AiUsageLogTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AiUsageLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AiUsageLog.class);
        AiUsageLog aiUsageLog1 = getAiUsageLogSample1();
        AiUsageLog aiUsageLog2 = new AiUsageLog();
        assertThat(aiUsageLog1).isNotEqualTo(aiUsageLog2);

        aiUsageLog2.setId(aiUsageLog1.getId());
        assertThat(aiUsageLog1).isEqualTo(aiUsageLog2);

        aiUsageLog2 = getAiUsageLogSample2();
        assertThat(aiUsageLog1).isNotEqualTo(aiUsageLog2);
    }

    @Test
    void userTest() {
        AiUsageLog aiUsageLog = getAiUsageLogRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        aiUsageLog.setUser(profileBack);
        assertThat(aiUsageLog.getUser()).isEqualTo(profileBack);

        aiUsageLog.user(null);
        assertThat(aiUsageLog.getUser()).isNull();
    }
}
