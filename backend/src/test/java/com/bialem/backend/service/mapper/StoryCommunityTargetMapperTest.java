package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.StoryCommunityTargetAsserts.*;
import static com.bialem.backend.domain.StoryCommunityTargetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoryCommunityTargetMapperTest {

    private StoryCommunityTargetMapper storyCommunityTargetMapper;

    @BeforeEach
    void setUp() {
        storyCommunityTargetMapper = new StoryCommunityTargetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStoryCommunityTargetSample1();
        var actual = storyCommunityTargetMapper.toEntity(storyCommunityTargetMapper.toDto(expected));
        assertStoryCommunityTargetAllPropertiesEquals(expected, actual);
    }
}
