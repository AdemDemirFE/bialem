package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.EventMessageAsserts.*;
import static com.bialem.backend.domain.EventMessageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventMessageMapperTest {

    private EventMessageMapper eventMessageMapper;

    @BeforeEach
    void setUp() {
        eventMessageMapper = new EventMessageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventMessageSample1();
        var actual = eventMessageMapper.toEntity(eventMessageMapper.toDto(expected));
        assertEventMessageAllPropertiesEquals(expected, actual);
    }
}
