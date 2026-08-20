package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerOffer;
import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.service.dto.PartnerOfferDTO;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T19:18:45+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PartnerOfferMapperImpl implements PartnerOfferMapper {

    @Override
    public PartnerOffer toEntity(PartnerOfferDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PartnerOffer partnerOffer = new PartnerOffer();

        partnerOffer.setId( dto.getId() );
        partnerOffer.setTitle( dto.getTitle() );
        partnerOffer.setDescription( dto.getDescription() );
        partnerOffer.setDiscountPercent( dto.getDiscountPercent() );
        partnerOffer.setMinimumSpend( dto.getMinimumSpend() );
        partnerOffer.setMaximumDiscount( dto.getMaximumDiscount() );
        partnerOffer.setValidFrom( dto.getValidFrom() );
        partnerOffer.setValidUntil( dto.getValidUntil() );
        partnerOffer.setValidDays( dto.getValidDays() );
        partnerOffer.setDailyStartTime( dto.getDailyStartTime() );
        partnerOffer.setDailyEndTime( dto.getDailyEndTime() );
        partnerOffer.setPerUserLimit( dto.getPerUserLimit() );
        partnerOffer.setTerms( dto.getTerms() );
        partnerOffer.setIsActive( dto.getIsActive() );
        partnerOffer.setCreatedAt( dto.getCreatedAt() );
        partnerOffer.setUpdatedAt( dto.getUpdatedAt() );
        partnerOffer.venue( partnerVenueDTOToPartnerVenue( dto.getVenue() ) );

        return partnerOffer;
    }

    @Override
    public List<PartnerOffer> toEntity(List<PartnerOfferDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PartnerOffer> list = new ArrayList<PartnerOffer>( dtoList.size() );
        for ( PartnerOfferDTO partnerOfferDTO : dtoList ) {
            list.add( toEntity( partnerOfferDTO ) );
        }

        return list;
    }

    @Override
    public List<PartnerOfferDTO> toDto(List<PartnerOffer> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PartnerOfferDTO> list = new ArrayList<PartnerOfferDTO>( entityList.size() );
        for ( PartnerOffer partnerOffer : entityList ) {
            list.add( toDto( partnerOffer ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PartnerOffer entity, PartnerOfferDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getTitle() != null ) {
            entity.setTitle( dto.getTitle() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getDiscountPercent() != null ) {
            entity.setDiscountPercent( dto.getDiscountPercent() );
        }
        if ( dto.getMinimumSpend() != null ) {
            entity.setMinimumSpend( dto.getMinimumSpend() );
        }
        if ( dto.getMaximumDiscount() != null ) {
            entity.setMaximumDiscount( dto.getMaximumDiscount() );
        }
        if ( dto.getValidFrom() != null ) {
            entity.setValidFrom( dto.getValidFrom() );
        }
        if ( dto.getValidUntil() != null ) {
            entity.setValidUntil( dto.getValidUntil() );
        }
        if ( dto.getValidDays() != null ) {
            entity.setValidDays( dto.getValidDays() );
        }
        if ( dto.getDailyStartTime() != null ) {
            entity.setDailyStartTime( dto.getDailyStartTime() );
        }
        if ( dto.getDailyEndTime() != null ) {
            entity.setDailyEndTime( dto.getDailyEndTime() );
        }
        if ( dto.getPerUserLimit() != null ) {
            entity.setPerUserLimit( dto.getPerUserLimit() );
        }
        if ( dto.getTerms() != null ) {
            entity.setTerms( dto.getTerms() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getVenue() != null ) {
            if ( entity.getVenue() == null ) {
                entity.venue( new PartnerVenue() );
            }
            partnerVenueDTOToPartnerVenue1( dto.getVenue(), entity.getVenue() );
        }
    }

    @Override
    public PartnerOfferDTO toDto(PartnerOffer s) {
        if ( s == null ) {
            return null;
        }

        PartnerOfferDTO partnerOfferDTO = new PartnerOfferDTO();

        partnerOfferDTO.setVenue( toDtoPartnerVenueId( s.getVenue() ) );
        partnerOfferDTO.setId( s.getId() );
        partnerOfferDTO.setTitle( s.getTitle() );
        partnerOfferDTO.setDescription( s.getDescription() );
        partnerOfferDTO.setDiscountPercent( s.getDiscountPercent() );
        partnerOfferDTO.setMinimumSpend( s.getMinimumSpend() );
        partnerOfferDTO.setMaximumDiscount( s.getMaximumDiscount() );
        partnerOfferDTO.setValidFrom( s.getValidFrom() );
        partnerOfferDTO.setValidUntil( s.getValidUntil() );
        partnerOfferDTO.setValidDays( s.getValidDays() );
        partnerOfferDTO.setDailyStartTime( s.getDailyStartTime() );
        partnerOfferDTO.setDailyEndTime( s.getDailyEndTime() );
        partnerOfferDTO.setPerUserLimit( s.getPerUserLimit() );
        partnerOfferDTO.setTerms( s.getTerms() );
        partnerOfferDTO.setIsActive( s.getIsActive() );
        partnerOfferDTO.setCreatedAt( s.getCreatedAt() );
        partnerOfferDTO.setUpdatedAt( s.getUpdatedAt() );

        return partnerOfferDTO;
    }

    @Override
    public PartnerVenueDTO toDtoPartnerVenueId(PartnerVenue partnerVenue) {
        if ( partnerVenue == null ) {
            return null;
        }

        PartnerVenueDTO partnerVenueDTO = new PartnerVenueDTO();

        partnerVenueDTO.setId( partnerVenue.getId() );

        return partnerVenueDTO;
    }

    protected PartnerVenue partnerVenueDTOToPartnerVenue(PartnerVenueDTO partnerVenueDTO) {
        if ( partnerVenueDTO == null ) {
            return null;
        }

        PartnerVenue partnerVenue = new PartnerVenue();

        partnerVenue.setId( partnerVenueDTO.getId() );
        partnerVenue.setName( partnerVenueDTO.getName() );
        partnerVenue.setSlug( partnerVenueDTO.getSlug() );
        partnerVenue.setDescription( partnerVenueDTO.getDescription() );
        partnerVenue.setCategory( partnerVenueDTO.getCategory() );
        partnerVenue.setLogoUrl( partnerVenueDTO.getLogoUrl() );
        partnerVenue.setCoverImageUrl( partnerVenueDTO.getCoverImageUrl() );
        partnerVenue.setAddress( partnerVenueDTO.getAddress() );
        partnerVenue.setCity( partnerVenueDTO.getCity() );
        partnerVenue.setLatitude( partnerVenueDTO.getLatitude() );
        partnerVenue.setLongitude( partnerVenueDTO.getLongitude() );
        partnerVenue.setPhone( partnerVenueDTO.getPhone() );
        partnerVenue.setWebsiteUrl( partnerVenueDTO.getWebsiteUrl() );
        partnerVenue.setInstagramUrl( partnerVenueDTO.getInstagramUrl() );
        partnerVenue.setIsFeatured( partnerVenueDTO.getIsFeatured() );
        partnerVenue.setIsActive( partnerVenueDTO.getIsActive() );
        partnerVenue.setCreatedAt( partnerVenueDTO.getCreatedAt() );
        partnerVenue.setUpdatedAt( partnerVenueDTO.getUpdatedAt() );

        return partnerVenue;
    }

    protected void partnerVenueDTOToPartnerVenue1(PartnerVenueDTO partnerVenueDTO, PartnerVenue mappingTarget) {
        if ( partnerVenueDTO == null ) {
            return;
        }

        if ( partnerVenueDTO.getId() != null ) {
            mappingTarget.setId( partnerVenueDTO.getId() );
        }
        if ( partnerVenueDTO.getName() != null ) {
            mappingTarget.setName( partnerVenueDTO.getName() );
        }
        if ( partnerVenueDTO.getSlug() != null ) {
            mappingTarget.setSlug( partnerVenueDTO.getSlug() );
        }
        if ( partnerVenueDTO.getDescription() != null ) {
            mappingTarget.setDescription( partnerVenueDTO.getDescription() );
        }
        if ( partnerVenueDTO.getCategory() != null ) {
            mappingTarget.setCategory( partnerVenueDTO.getCategory() );
        }
        if ( partnerVenueDTO.getLogoUrl() != null ) {
            mappingTarget.setLogoUrl( partnerVenueDTO.getLogoUrl() );
        }
        if ( partnerVenueDTO.getCoverImageUrl() != null ) {
            mappingTarget.setCoverImageUrl( partnerVenueDTO.getCoverImageUrl() );
        }
        if ( partnerVenueDTO.getAddress() != null ) {
            mappingTarget.setAddress( partnerVenueDTO.getAddress() );
        }
        if ( partnerVenueDTO.getCity() != null ) {
            mappingTarget.setCity( partnerVenueDTO.getCity() );
        }
        if ( partnerVenueDTO.getLatitude() != null ) {
            mappingTarget.setLatitude( partnerVenueDTO.getLatitude() );
        }
        if ( partnerVenueDTO.getLongitude() != null ) {
            mappingTarget.setLongitude( partnerVenueDTO.getLongitude() );
        }
        if ( partnerVenueDTO.getPhone() != null ) {
            mappingTarget.setPhone( partnerVenueDTO.getPhone() );
        }
        if ( partnerVenueDTO.getWebsiteUrl() != null ) {
            mappingTarget.setWebsiteUrl( partnerVenueDTO.getWebsiteUrl() );
        }
        if ( partnerVenueDTO.getInstagramUrl() != null ) {
            mappingTarget.setInstagramUrl( partnerVenueDTO.getInstagramUrl() );
        }
        if ( partnerVenueDTO.getIsFeatured() != null ) {
            mappingTarget.setIsFeatured( partnerVenueDTO.getIsFeatured() );
        }
        if ( partnerVenueDTO.getIsActive() != null ) {
            mappingTarget.setIsActive( partnerVenueDTO.getIsActive() );
        }
        if ( partnerVenueDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( partnerVenueDTO.getCreatedAt() );
        }
        if ( partnerVenueDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( partnerVenueDTO.getUpdatedAt() );
        }
    }
}
