package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.domain.CityEventInterest;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.CityEventDTO;
import com.bialem.backend.service.dto.CityEventInterestDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CityEventInterest} and its DTO {@link CityEventInterestDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityEventInterestMapper extends EntityMapper<CityEventInterestDTO, CityEventInterest> {
    @Mapping(target = "cityEvent", source = "cityEvent", qualifiedByName = "cityEventId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    CityEventInterestDTO toDto(CityEventInterest s);

    @Named("cityEventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CityEventDTO toDtoCityEventId(CityEvent cityEvent);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
