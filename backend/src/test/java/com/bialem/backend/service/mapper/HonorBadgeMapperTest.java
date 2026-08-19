package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.HonorBadgeAsserts.*;
import static com.bialem.backend.domain.HonorBadgeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HonorBadgeMapperTest {

    private HonorBadgeMapper honorBadgeMapper;

    @BeforeEach
    void setUp() {
        honorBadgeMapper = new HonorBadgeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHonorBadgeSample1();
        var actual = honorBadgeMapper.toEntity(honorBadgeMapper.toDto(expected));
        assertHonorBadgeAllPropertiesEquals(expected, actual);
    }
}
