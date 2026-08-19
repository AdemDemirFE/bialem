package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Story;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.StoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Story} and its DTO {@link StoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface StoryMapper extends EntityMapper<StoryDTO, Story> {
    @Mapping(target = "author", source = "author", qualifiedByName = "profileId")
    StoryDTO toDto(Story s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
