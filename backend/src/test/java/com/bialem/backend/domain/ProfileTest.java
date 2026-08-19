package com.bialem.backend.domain;

import static com.bialem.backend.domain.AccountPreferencesTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Profile.class);
        Profile profile1 = getProfileSample1();
        Profile profile2 = new Profile();
        assertThat(profile1).isNotEqualTo(profile2);

        profile2.setId(profile1.getId());
        assertThat(profile1).isEqualTo(profile2);

        profile2 = getProfileSample2();
        assertThat(profile1).isNotEqualTo(profile2);
    }

    @Test
    void preferencesTest() {
        Profile profile = getProfileRandomSampleGenerator();
        AccountPreferences accountPreferencesBack = getAccountPreferencesRandomSampleGenerator();

        profile.setPreferences(accountPreferencesBack);
        assertThat(profile.getPreferences()).isEqualTo(accountPreferencesBack);
        assertThat(accountPreferencesBack.getProfile()).isEqualTo(profile);

        profile.preferences(null);
        assertThat(profile.getPreferences()).isNull();
        assertThat(accountPreferencesBack.getProfile()).isNull();
    }
}
