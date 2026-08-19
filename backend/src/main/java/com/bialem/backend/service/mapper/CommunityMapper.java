package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Community} and its DTO {@link CommunityDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommunityMapper extends EntityMapper<CommunityDTO, Community> {
    @Mapping(target = "parent", source = "parent", qualifiedByName = "communityId")
    @Mapping(target = "categoryHub", source = "categoryHub", qualifiedByName = "communityId")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "profileId")
    @Mapping(target = "leadModerator", source = "leadModerator", qualifiedByName = "profileId")
    CommunityDTO toDto(Community s);

    @Named("communityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityDTO toDtoCommunityId(Community community);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
