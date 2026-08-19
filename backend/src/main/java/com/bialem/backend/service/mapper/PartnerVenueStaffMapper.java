package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.domain.PartnerVenueStaff;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.dto.PartnerVenueStaffDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartnerVenueStaff} and its DTO {@link PartnerVenueStaffDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartnerVenueStaffMapper extends EntityMapper<PartnerVenueStaffDTO, PartnerVenueStaff> {
    @Mapping(target = "venue", source = "venue", qualifiedByName = "partnerVenueId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    PartnerVenueStaffDTO toDto(PartnerVenueStaff s);

    @Named("partnerVenueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PartnerVenueDTO toDtoPartnerVenueId(PartnerVenue partnerVenue);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
