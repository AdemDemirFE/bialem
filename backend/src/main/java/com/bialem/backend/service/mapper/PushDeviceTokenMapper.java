package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PushDeviceToken;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.PushDeviceTokenDTO;
import com.bialem.backend.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PushDeviceToken} and its DTO {@link PushDeviceTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface PushDeviceTokenMapper extends EntityMapper<PushDeviceTokenDTO, PushDeviceToken> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userId")
    PushDeviceTokenDTO toDto(PushDeviceToken s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);
}
