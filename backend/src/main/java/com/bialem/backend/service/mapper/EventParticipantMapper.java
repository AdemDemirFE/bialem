package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.EventParticipant;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.EventParticipantDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EventParticipant} and its DTO {@link EventParticipantDTO}.
 */
@Mapper(componentModel = "spring")
public interface EventParticipantMapper extends EntityMapper<EventParticipantDTO, EventParticipant> {
    @Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    EventParticipantDTO toDto(EventParticipant s);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
