package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.EventRatingAsserts.*;
import static com.bialem.backend.domain.EventRatingTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventRatingMapperTest {

    private EventRatingMapper eventRatingMapper;

    @BeforeEach
    void setUp() {
        eventRatingMapper = new EventRatingMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventRatingSample1();
        var actual = eventRatingMapper.toEntity(eventRatingMapper.toDto(expected));
        assertEventRatingAllPropertiesEquals(expected, actual);
    }
}
