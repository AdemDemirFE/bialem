package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.StoryView;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.StoryDTO;
import com.bialem.backend.service.dto.StoryViewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StoryView} and its DTO {@link StoryViewDTO}.
 */
@Mapper(componentModel = "spring")
public interface StoryViewMapper extends EntityMapper<StoryViewDTO, StoryView> {
    @Mapping(target = "story", source = "story", qualifiedByName = "storyId")
    @Mapping(target = "viewer", source = "viewer", qualifiedByName = "profileId")
    StoryViewDTO toDto(StoryView s);

    @Named("storyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StoryDTO toDtoStoryId(Story story);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
