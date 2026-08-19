package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PostMediaAsserts.*;
import static com.bialem.backend.domain.PostMediaTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostMediaMapperTest {

    private PostMediaMapper postMediaMapper;

    @BeforeEach
    void setUp() {
        postMediaMapper = new PostMediaMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPostMediaSample1();
        var actual = postMediaMapper.toEntity(postMediaMapper.toDto(expected));
        assertPostMediaAllPropertiesEquals(expected, actual);
    }
}
