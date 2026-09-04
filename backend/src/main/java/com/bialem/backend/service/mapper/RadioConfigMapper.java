package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.RadioConfig;
import com.bialem.backend.service.dto.RadioConfigDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RadioConfig} and its DTO {@link RadioConfigDTO}.
 */
@Mapper(componentModel = "spring")
public interface RadioConfigMapper extends EntityMapper<RadioConfigDTO, RadioConfig> {}
