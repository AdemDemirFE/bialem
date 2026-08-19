package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.CommunityModeratorAssistantAsserts.*;
import static com.bialem.backend.domain.CommunityModeratorAssistantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommunityModeratorAssistantMapperTest {

    private CommunityModeratorAssistantMapper communityModeratorAssistantMapper;

    @BeforeEach
    void setUp() {
        communityModeratorAssistantMapper = new CommunityModeratorAssistantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCommunityModeratorAssistantSample1();
        var actual = communityModeratorAssistantMapper.toEntity(communityModeratorAssistantMapper.toDto(expected));
        assertCommunityModeratorAssistantAllPropertiesEquals(expected, actual);
    }
}
