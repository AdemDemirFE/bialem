package com.bialem.backend.domain;

import static com.bialem.backend.domain.NotificationTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void userTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        notification.setUser(profileBack);
        assertThat(notification.getUser()).isEqualTo(profileBack);

        notification.user(null);
        assertThat(notification.getUser()).isNull();
    }
}
