package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerOffer;
import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.service.dto.PartnerOfferDTO;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartnerOffer} and its DTO {@link PartnerOfferDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartnerOfferMapper extends EntityMapper<PartnerOfferDTO, PartnerOffer> {
    @Mapping(target = "venue", source = "venue", qualifiedByName = "partnerVenueId")
    PartnerOfferDTO toDto(PartnerOffer s);

    @Named("partnerVenueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PartnerVenueDTO toDtoPartnerVenueId(PartnerVenue partnerVenue);
}
