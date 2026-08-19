package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.UserReviewAsserts.*;
import static com.bialem.backend.domain.UserReviewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserReviewMapperTest {

    private UserReviewMapper userReviewMapper;

    @BeforeEach
    void setUp() {
        userReviewMapper = new UserReviewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getUserReviewSample1();
        var actual = userReviewMapper.toEntity(userReviewMapper.toDto(expected));
        assertUserReviewAllPropertiesEquals(expected, actual);
    }
}
