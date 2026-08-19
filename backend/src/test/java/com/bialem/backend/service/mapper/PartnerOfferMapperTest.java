package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PartnerOfferAsserts.*;
import static com.bialem.backend.domain.PartnerOfferTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartnerOfferMapperTest {

    private PartnerOfferMapper partnerOfferMapper;

    @BeforeEach
    void setUp() {
        partnerOfferMapper = new PartnerOfferMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartnerOfferSample1();
        var actual = partnerOfferMapper.toEntity(partnerOfferMapper.toDto(expected));
        assertPartnerOfferAllPropertiesEquals(expected, actual);
    }
}
