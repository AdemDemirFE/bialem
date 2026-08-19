package com.bialem.backend.domain;

import static com.bialem.backend.domain.ProfileTestSamples.*;
import static com.bialem.backend.domain.PushTokenTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PushTokenTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PushToken.class);
        PushToken pushToken1 = getPushTokenSample1();
        PushToken pushToken2 = new PushToken();
        assertThat(pushToken1).isNotEqualTo(pushToken2);

        pushToken2.setId(pushToken1.getId());
        assertThat(pushToken1).isEqualTo(pushToken2);

        pushToken2 = getPushTokenSample2();
        assertThat(pushToken1).isNotEqualTo(pushToken2);
    }

    @Test
    void userTest() {
        PushToken pushToken = getPushTokenRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        pushToken.setUser(profileBack);
        assertThat(pushToken.getUser()).isEqualTo(profileBack);

        pushToken.user(null);
        assertThat(pushToken.getUser()).isNull();
    }
}
