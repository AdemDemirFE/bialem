package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.StoryAsserts.*;
import static com.bialem.backend.domain.StoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoryMapperTest {

    private StoryMapper storyMapper;

    @BeforeEach
    void setUp() {
        storyMapper = new StoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStorySample1();
        var actual = storyMapper.toEntity(storyMapper.toDto(expected));
        assertStoryAllPropertiesEquals(expected, actual);
    }
}
