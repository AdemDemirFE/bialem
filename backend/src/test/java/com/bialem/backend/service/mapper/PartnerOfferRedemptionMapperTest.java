package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PartnerOfferRedemptionAsserts.*;
import static com.bialem.backend.domain.PartnerOfferRedemptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartnerOfferRedemptionMapperTest {

    private PartnerOfferRedemptionMapper partnerOfferRedemptionMapper;

    @BeforeEach
    void setUp() {
        partnerOfferRedemptionMapper = new PartnerOfferRedemptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartnerOfferRedemptionSample1();
        var actual = partnerOfferRedemptionMapper.toEntity(partnerOfferRedemptionMapper.toDto(expected));
        assertPartnerOfferRedemptionAllPropertiesEquals(expected, actual);
    }
}
