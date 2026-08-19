package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PlatformTeamMemberAsserts.*;
import static com.bialem.backend.domain.PlatformTeamMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlatformTeamMemberMapperTest {

    private PlatformTeamMemberMapper platformTeamMemberMapper;

    @BeforeEach
    void setUp() {
        platformTeamMemberMapper = new PlatformTeamMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPlatformTeamMemberSample1();
        var actual = platformTeamMemberMapper.toEntity(platformTeamMemberMapper.toDto(expected));
        assertPlatformTeamMemberAllPropertiesEquals(expected, actual);
    }
}
