package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.FollowRequest;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.FollowRequestDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FollowRequest} and its DTO {@link FollowRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface FollowRequestMapper extends EntityMapper<FollowRequestDTO, FollowRequest> {
    @Mapping(target = "requester", source = "requester", qualifiedByName = "profileId")
    @Mapping(target = "targetUser", source = "targetUser", qualifiedByName = "profileId")
    FollowRequestDTO toDto(FollowRequest s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
