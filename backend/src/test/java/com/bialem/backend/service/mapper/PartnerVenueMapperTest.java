package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PartnerVenueAsserts.*;
import static com.bialem.backend.domain.PartnerVenueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartnerVenueMapperTest {

    private PartnerVenueMapper partnerVenueMapper;

    @BeforeEach
    void setUp() {
        partnerVenueMapper = new PartnerVenueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartnerVenueSample1();
        var actual = partnerVenueMapper.toEntity(partnerVenueMapper.toDto(expected));
        assertPartnerVenueAllPropertiesEquals(expected, actual);
    }
}
