package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.CommunityMemberAsserts.*;
import static com.bialem.backend.domain.CommunityMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CommunityMemberMapperTest {

    private CommunityMemberMapper communityMemberMapper;

    @BeforeEach
    void setUp() {
        communityMemberMapper = new CommunityMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCommunityMemberSample1();
        var actual = communityMemberMapper.toEntity(communityMemberMapper.toDto(expected));
        assertCommunityMemberAllPropertiesEquals(expected, actual);
    }
}
