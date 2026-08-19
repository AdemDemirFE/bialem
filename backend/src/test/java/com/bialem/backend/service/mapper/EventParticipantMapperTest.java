package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.EventParticipantAsserts.*;
import static com.bialem.backend.domain.EventParticipantTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EventParticipantMapperTest {

    private EventParticipantMapper eventParticipantMapper;

    @BeforeEach
    void setUp() {
        eventParticipantMapper = new EventParticipantMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEventParticipantSample1();
        var actual = eventParticipantMapper.toEntity(eventParticipantMapper.toDto(expected));
        assertEventParticipantAllPropertiesEquals(expected, actual);
    }
}
