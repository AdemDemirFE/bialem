package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEventSyncLog;
import com.bialem.backend.service.dto.CityEventSyncLogDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CityEventSyncLog} and its DTO {@link CityEventSyncLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityEventSyncLogMapper extends EntityMapper<CityEventSyncLogDTO, CityEventSyncLog> {}
