package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.UserReview;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserReview} and its DTO {@link UserReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserReviewMapper extends EntityMapper<UserReviewDTO, UserReview> {
    @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "profileId")
    @Mapping(target = "reviewedUser", source = "reviewedUser", qualifiedByName = "profileId")
    @Mapping(target = "event", source = "event", qualifiedByName = "eventId")
    UserReviewDTO toDto(UserReview s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("eventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    EventDTO toDtoEventId(Event event);
}
