package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.CityEventSyncLogAsserts.*;
import static com.bialem.backend.domain.CityEventSyncLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CityEventSyncLogMapperTest {

    private CityEventSyncLogMapper cityEventSyncLogMapper;

    @BeforeEach
    void setUp() {
        cityEventSyncLogMapper = new CityEventSyncLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCityEventSyncLogSample1();
        var actual = cityEventSyncLogMapper.toEntity(cityEventSyncLogMapper.toDto(expected));
        assertCityEventSyncLogAllPropertiesEquals(expected, actual);
    }
}
