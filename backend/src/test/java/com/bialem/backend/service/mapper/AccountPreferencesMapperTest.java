package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.AccountPreferencesAsserts.*;
import static com.bialem.backend.domain.AccountPreferencesTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountPreferencesMapperTest {

    private AccountPreferencesMapper accountPreferencesMapper;

    @BeforeEach
    void setUp() {
        accountPreferencesMapper = new AccountPreferencesMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAccountPreferencesSample1();
        var actual = accountPreferencesMapper.toEntity(accountPreferencesMapper.toDto(expected));
        assertAccountPreferencesAllPropertiesEquals(expected, actual);
    }
}
