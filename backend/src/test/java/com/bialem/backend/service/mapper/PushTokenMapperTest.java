package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PushTokenAsserts.*;
import static com.bialem.backend.domain.PushTokenTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PushTokenMapperTest {

    private PushTokenMapper pushTokenMapper;

    @BeforeEach
    void setUp() {
        pushTokenMapper = new PushTokenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPushTokenSample1();
        var actual = pushTokenMapper.toEntity(pushTokenMapper.toDto(expected));
        assertPushTokenAllPropertiesEquals(expected, actual);
    }
}
