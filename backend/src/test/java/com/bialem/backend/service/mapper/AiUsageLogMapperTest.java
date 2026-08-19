package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.AiUsageLogAsserts.*;
import static com.bialem.backend.domain.AiUsageLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiUsageLogMapperTest {

    private AiUsageLogMapper aiUsageLogMapper;

    @BeforeEach
    void setUp() {
        aiUsageLogMapper = new AiUsageLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAiUsageLogSample1();
        var actual = aiUsageLogMapper.toEntity(aiUsageLogMapper.toDto(expected));
        assertAiUsageLogAllPropertiesEquals(expected, actual);
    }
}
