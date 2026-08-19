package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.CityEventAsserts.*;
import static com.bialem.backend.domain.CityEventTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CityEventMapperTest {

    private CityEventMapper cityEventMapper;

    @BeforeEach
    void setUp() {
        cityEventMapper = new CityEventMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCityEventSample1();
        var actual = cityEventMapper.toEntity(cityEventMapper.toDto(expected));
        assertCityEventAllPropertiesEquals(expected, actual);
    }
}
