package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.UserHonorBadgeAsserts.*;
import static com.bialem.backend.domain.UserHonorBadgeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserHonorBadgeMapperTest {

    private UserHonorBadgeMapper userHonorBadgeMapper;

    @BeforeEach
    void setUp() {
        userHonorBadgeMapper = new UserHonorBadgeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserHonorBadgeSample1();
        var actual = userHonorBadgeMapper.toEntity(userHonorBadgeMapper.toDto(expected));
        assertUserHonorBadgeAllPropertiesEquals(expected, actual);
    }
}
