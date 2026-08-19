package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.AiUsageLog;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.AiUsageLogDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AiUsageLog} and its DTO {@link AiUsageLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface AiUsageLogMapper extends EntityMapper<AiUsageLogDTO, AiUsageLog> {
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    AiUsageLogDTO toDto(AiUsageLog s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
