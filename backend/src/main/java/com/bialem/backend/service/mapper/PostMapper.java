package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.Post;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.PostDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for the entity {@link Post} and its DTO {@link PostDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper extends EntityMapper<PostDTO, Post> {
    @Mapping(target = "community", source = "community", qualifiedByName = "communityId")
    @Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    @Mapping(target = "author", source = "author", qualifiedByName = "profileId")
    PostDTO toDto(Post s);

    @Named("communityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityDTO toDtoCommunityId(Community community);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
