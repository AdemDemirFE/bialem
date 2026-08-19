package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.StoryCommunityTarget;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.StoryCommunityTargetDTO;
import com.bialem.backend.service.dto.StoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StoryCommunityTarget} and its DTO {@link StoryCommunityTargetDTO}.
 */
@Mapper(componentModel = "spring")
public interface StoryCommunityTargetMapper extends EntityMapper<StoryCommunityTargetDTO, StoryCommunityTarget> {
    @Mapping(target = "story", source = "story", qualifiedByName = "storyId")
    @Mapping(target = "community", source = "community", qualifiedByName = "communityId")
    StoryCommunityTargetDTO toDto(StoryCommunityTarget s);

    @Named("storyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StoryDTO toDtoStoryId(Story story);

    @Named("communityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityDTO toDtoCommunityId(Community community);
}
