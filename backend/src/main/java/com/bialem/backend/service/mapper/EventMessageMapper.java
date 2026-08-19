package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.EventMessage;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.EventMessageDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventMessage} and its DTO {@link EventMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventMessageMapper extends EntityMapper<EventMessageDTO, EventMessage> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    @Mapping(target = "author", source = "author", qualifiedByName = "profileId")
    EventMessageDTO toDto(EventMessage s);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
