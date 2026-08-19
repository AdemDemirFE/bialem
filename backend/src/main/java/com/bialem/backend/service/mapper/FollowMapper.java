package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Follow;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.FollowDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Follow} and its DTO {@link FollowDTO}.
 */
@Mapper(componentModel = "spring")
public interface FollowMapper extends EntityMapper<FollowDTO, Follow> {
    @Mapping(target = "follower", source = "follower", qualifiedByName = "profileId")
    @Mapping(target = "followed", source = "followed", qualifiedByName = "profileId")
    FollowDTO toDto(Follow s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
