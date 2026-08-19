package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.CityEventTicketOfferAsserts.*;
import static com.bialem.backend.domain.CityEventTicketOfferTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CityEventTicketOfferMapperTest {

    private CityEventTicketOfferMapper cityEventTicketOfferMapper;

    @BeforeEach
    void setUp() {
        cityEventTicketOfferMapper = new CityEventTicketOfferMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCityEventTicketOfferSample1();
        var actual = cityEventTicketOfferMapper.toEntity(cityEventTicketOfferMapper.toDto(expected));
        assertCityEventTicketOfferAllPropertiesEquals(expected, actual);
    }
}
