package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartnerVenue} and its DTO {@link PartnerVenueDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartnerVenueMapper extends EntityMapper<PartnerVenueDTO, PartnerVenue> {}
