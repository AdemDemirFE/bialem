package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.CommunityModeratorAssistant;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.CommunityModeratorAssistantDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CommunityModeratorAssistant} and its DTO {@link CommunityModeratorAssistantDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommunityModeratorAssistantMapper extends EntityMapper<CommunityModeratorAssistantDTO, CommunityModeratorAssistant> {
    @Mapping(target = "community", source = "community", qualifiedByName = "communityId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    CommunityModeratorAssistantDTO toDto(CommunityModeratorAssistant s);

    @Named("communityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityDTO toDtoCommunityId(Community community);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
