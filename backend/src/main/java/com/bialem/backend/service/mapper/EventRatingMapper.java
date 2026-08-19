package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.EventRating;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.EventRatingDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventRating} and its DTO {@link EventRatingDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventRatingMapper extends EntityMapper<EventRatingDTO, EventRating> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    EventRatingDTO toDto(EventRating s);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
