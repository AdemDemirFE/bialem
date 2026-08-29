package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.StoryReaction;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.StoryDTO;
import com.bialem.backend.service.dto.StoryReactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StoryReaction} and its DTO {@link StoryReactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface StoryReactionMapper extends EntityMapper<StoryReactionDTO, StoryReaction> {
    @Mapping(target = "story", source = "story", qualifiedByName = "storyId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    StoryReactionDTO toDto(StoryReaction s);

    @Named("storyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StoryDTO toDtoStoryId(Story story);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
