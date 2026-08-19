package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.StoryViewAsserts.*;
import static com.bialem.backend.domain.StoryViewTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoryViewMapperTest {

    private StoryViewMapper storyViewMapper;

    @BeforeEach
    void setUp() {
        storyViewMapper = new StoryViewMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStoryViewSample1();
        var actual = storyViewMapper.toEntity(storyViewMapper.toDto(expected));
        assertStoryViewAllPropertiesEquals(expected, actual);
    }
}
