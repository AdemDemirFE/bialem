package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerOffer;
import com.bialem.backend.domain.PartnerOfferRedemption;
import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.PartnerOfferDTO;
import com.bialem.backend.service.dto.PartnerOfferRedemptionDTO;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PartnerOfferRedemption} and its DTO {@link PartnerOfferRedemptionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PartnerOfferRedemptionMapper extends EntityMapper<PartnerOfferRedemptionDTO, PartnerOfferRedemption> {
    @Mapping(target = "offer", source = "offer", qualifiedByName = "partnerOfferId")
    @Mapping(target = "venue", source = "venue", qualifiedByName = "partnerVenueId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    @Mapping(target = "redeemedBy", source = "redeemedBy", qualifiedByName = "profileId")
    PartnerOfferRedemptionDTO toDto(PartnerOfferRedemption s);

    @Named("partnerOfferId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PartnerOfferDTO toDtoPartnerOfferId(PartnerOffer partnerOffer);

    @Named("partnerVenueId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PartnerVenueDTO toDtoPartnerVenueId(PartnerVenue partnerVenue);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
