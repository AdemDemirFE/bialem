package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T16:30:56+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PartnerVenueMapperImpl implements PartnerVenueMapper {

    @Override
    public PartnerVenue toEntity(PartnerVenueDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PartnerVenue partnerVenue = new PartnerVenue();

        partnerVenue.setId( dto.getId() );
        partnerVenue.setName( dto.getName() );
        partnerVenue.setSlug( dto.getSlug() );
        partnerVenue.setDescription( dto.getDescription() );
        partnerVenue.setCategory( dto.getCategory() );
        partnerVenue.setLogoUrl( dto.getLogoUrl() );
        partnerVenue.setCoverImageUrl( dto.getCoverImageUrl() );
        partnerVenue.setAddress( dto.getAddress() );
        partnerVenue.setCity( dto.getCity() );
        partnerVenue.setLatitude( dto.getLatitude() );
        partnerVenue.setLongitude( dto.getLongitude() );
        partnerVenue.setPhone( dto.getPhone() );
        partnerVenue.setWebsiteUrl( dto.getWebsiteUrl() );
        partnerVenue.setInstagramUrl( dto.getInstagramUrl() );
        partnerVenue.setIsFeatured( dto.getIsFeatured() );
        partnerVenue.setIsActive( dto.getIsActive() );
        partnerVenue.setCreatedAt( dto.getCreatedAt() );
        partnerVenue.setUpdatedAt( dto.getUpdatedAt() );

        return partnerVenue;
    }

    @Override
    public PartnerVenueDTO toDto(PartnerVenue entity) {
        if ( entity == null ) {
            return null;
        }

        PartnerVenueDTO partnerVenueDTO = new PartnerVenueDTO();

        partnerVenueDTO.setId( entity.getId() );
        partnerVenueDTO.setName( entity.getName() );
        partnerVenueDTO.setSlug( entity.getSlug() );
        partnerVenueDTO.setDescription( entity.getDescription() );
        partnerVenueDTO.setCategory( entity.getCategory() );
        partnerVenueDTO.setLogoUrl( entity.getLogoUrl() );
        partnerVenueDTO.setCoverImageUrl( entity.getCoverImageUrl() );
        partnerVenueDTO.setAddress( entity.getAddress() );
        partnerVenueDTO.setCity( entity.getCity() );
        partnerVenueDTO.setLatitude( entity.getLatitude() );
        partnerVenueDTO.setLongitude( entity.getLongitude() );
        partnerVenueDTO.setPhone( entity.getPhone() );
        partnerVenueDTO.setWebsiteUrl( entity.getWebsiteUrl() );
        partnerVenueDTO.setInstagramUrl( entity.getInstagramUrl() );
        partnerVenueDTO.setIsFeatured( entity.getIsFeatured() );
        partnerVenueDTO.setIsActive( entity.getIsActive() );
        partnerVenueDTO.setCreatedAt( entity.getCreatedAt() );
        partnerVenueDTO.setUpdatedAt( entity.getUpdatedAt() );

        return partnerVenueDTO;
    }

    @Override
    public List<PartnerVenue> toEntity(List<PartnerVenueDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PartnerVenue> list = new ArrayList<PartnerVenue>( dtoList.size() );
        for ( PartnerVenueDTO partnerVenueDTO : dtoList ) {
            list.add( toEntity( partnerVenueDTO ) );
        }

        return list;
    }

    @Override
    public List<PartnerVenueDTO> toDto(List<PartnerVenue> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PartnerVenueDTO> list = new ArrayList<PartnerVenueDTO>( entityList.size() );
        for ( PartnerVenue partnerVenue : entityList ) {
            list.add( toDto( partnerVenue ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PartnerVenue entity, PartnerVenueDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getName() != null ) {
            entity.setName( dto.getName() );
        }
        if ( dto.getSlug() != null ) {
            entity.setSlug( dto.getSlug() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getCategory() != null ) {
            entity.setCategory( dto.getCategory() );
        }
        if ( dto.getLogoUrl() != null ) {
            entity.setLogoUrl( dto.getLogoUrl() );
        }
        if ( dto.getCoverImageUrl() != null ) {
            entity.setCoverImageUrl( dto.getCoverImageUrl() );
        }
        if ( dto.getAddress() != null ) {
            entity.setAddress( dto.getAddress() );
        }
        if ( dto.getCity() != null ) {
            entity.setCity( dto.getCity() );
        }
        if ( dto.getLatitude() != null ) {
            entity.setLatitude( dto.getLatitude() );
        }
        if ( dto.getLongitude() != null ) {
            entity.setLongitude( dto.getLongitude() );
        }
        if ( dto.getPhone() != null ) {
            entity.setPhone( dto.getPhone() );
        }
        if ( dto.getWebsiteUrl() != null ) {
            entity.setWebsiteUrl( dto.getWebsiteUrl() );
        }
        if ( dto.getInstagramUrl() != null ) {
            entity.setInstagramUrl( dto.getInstagramUrl() );
        }
        if ( dto.getIsFeatured() != null ) {
            entity.setIsFeatured( dto.getIsFeatured() );
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
    }
}
