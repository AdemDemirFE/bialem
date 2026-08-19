package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AccountPreferencesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AccountPreferencesDTO.class);
        AccountPreferencesDTO accountPreferencesDTO1 = new AccountPreferencesDTO();
        accountPreferencesDTO1.setId(1L);
        AccountPreferencesDTO accountPreferencesDTO2 = new AccountPreferencesDTO();
        assertThat(accountPreferencesDTO1).isNotEqualTo(accountPreferencesDTO2);
        accountPreferencesDTO2.setId(accountPreferencesDTO1.getId());
        assertThat(accountPreferencesDTO1).isEqualTo(accountPreferencesDTO2);
        accountPreferencesDTO2.setId(2L);
        assertThat(accountPreferencesDTO1).isNotEqualTo(accountPreferencesDTO2);
        accountPreferencesDTO1.setId(null);
        assertThat(accountPreferencesDTO1).isNotEqualTo(accountPreferencesDTO2);
    }
}
