package com.bialem.backend.domain;

import static com.bialem.backend.domain.AccountPreferencesTestSamples.*;
import static com.bialem.backend.domain.ProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountPreferencesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountPreferences.class);
        AccountPreferences accountPreferences1 = getAccountPreferencesSample1();
        AccountPreferences accountPreferences2 = new AccountPreferences();
        assertThat(accountPreferences1).isNotEqualTo(accountPreferences2);

        accountPreferences2.setId(accountPreferences1.getId());
        assertThat(accountPreferences1).isEqualTo(accountPreferences2);

        accountPreferences2 = getAccountPreferencesSample2();
        assertThat(accountPreferences1).isNotEqualTo(accountPreferences2);
    }

    @Test
    void profileTest() {
        AccountPreferences accountPreferences = getAccountPreferencesRandomSampleGenerator();
        Profile profileBack = getProfileRandomSampleGenerator();

        accountPreferences.setProfile(profileBack);
        assertThat(accountPreferences.getProfile()).isEqualTo(profileBack);

        accountPreferences.profile(null);
        assertThat(accountPreferences.getProfile()).isNull();
    }
}
