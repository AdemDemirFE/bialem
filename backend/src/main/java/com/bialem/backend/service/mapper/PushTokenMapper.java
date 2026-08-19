package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.PushToken;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.PushTokenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PushToken} and its DTO {@link PushTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface PushTokenMapper extends EntityMapper<PushTokenDTO, PushToken> {
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    PushTokenDTO toDto(PushToken s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
