package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.PartnerVenueStaffAsserts.*;
import static com.bialem.backend.domain.PartnerVenueStaffTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PartnerVenueStaffMapperTest {

    private PartnerVenueStaffMapper partnerVenueStaffMapper;

    @BeforeEach
    void setUp() {
        partnerVenueStaffMapper = new PartnerVenueStaffMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPartnerVenueStaffSample1();
        var actual = partnerVenueStaffMapper.toEntity(partnerVenueStaffMapper.toDto(expected));
        assertPartnerVenueStaffAllPropertiesEquals(expected, actual);
    }
}
