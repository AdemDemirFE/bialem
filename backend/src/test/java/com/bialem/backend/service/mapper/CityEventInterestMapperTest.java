package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.CityEventInterestAsserts.*;
import static com.bialem.backend.domain.CityEventInterestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CityEventInterestMapperTest {

    private CityEventInterestMapper cityEventInterestMapper;

    @BeforeEach
    void setUp() {
        cityEventInterestMapper = new CityEventInterestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCityEventInterestSample1();
        var actual = cityEventInterestMapper.toEntity(cityEventInterestMapper.toDto(expected));
        assertCityEventInterestAllPropertiesEquals(expected, actual);
    }
}
