package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.FollowRequestAsserts.*;
import static com.bialem.backend.domain.FollowRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FollowRequestMapperTest {

    private FollowRequestMapper followRequestMapper;

    @BeforeEach
    void setUp() {
        followRequestMapper = new FollowRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFollowRequestSample1();
        var actual = followRequestMapper.toEntity(followRequestMapper.toDto(expected));
        assertFollowRequestAllPropertiesEquals(expected, actual);
    }
}
