package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.service.dto.CityEventDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CityEvent} and its DTO {@link CityEventDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityEventMapper extends EntityMapper<CityEventDTO, CityEvent> {}
