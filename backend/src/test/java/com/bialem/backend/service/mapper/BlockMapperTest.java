package com.bialem.backend.service.mapper;

import static com.bialem.backend.domain.BlockAsserts.*;
import static com.bialem.backend.domain.BlockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BlockMapperTest {

    private BlockMapper blockMapper;

    @BeforeEach
    void setUp() {
        blockMapper = new BlockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBlockSample1();
        var actual = blockMapper.toEntity(blockMapper.toDto(expected));
        assertBlockAllPropertiesEquals(expected, actual);
    }
}
