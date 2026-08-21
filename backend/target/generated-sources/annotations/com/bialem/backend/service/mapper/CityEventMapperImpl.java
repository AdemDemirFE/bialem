package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.service.dto.CityEventDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T22:10:20+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CityEventMapperImpl implements CityEventMapper {

    @Override
    public CityEvent toEntity(CityEventDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CityEvent cityEvent = new CityEvent();

        cityEvent.setId( dto.getId() );
        cityEvent.setTitle( dto.getTitle() );
        cityEvent.setDescription( dto.getDescription() );
        cityEvent.setCategory( dto.getCategory() );
        cityEvent.setCity( dto.getCity() );
        cityEvent.setVenueName( dto.getVenueName() );
        cityEvent.setAddressText( dto.getAddressText() );
        cityEvent.setStartsAt( dto.getStartsAt() );
        cityEvent.setEndsAt( dto.getEndsAt() );
        cityEvent.setCoverImageUrl( dto.getCoverImageUrl() );
        cityEvent.setPriceLabel( dto.getPriceLabel() );
        cityEvent.setSourceName( dto.getSourceName() );
        cityEvent.setSourceUrl( dto.getSourceUrl() );
        cityEvent.setTicketUrl( dto.getTicketUrl() );
        cityEvent.setStatus( dto.getStatus() );
        cityEvent.setProviderCode( dto.getProviderCode() );
        cityEvent.setExternalId( dto.getExternalId() );
        cityEvent.setLastSyncedAt( dto.getLastSyncedAt() );
        cityEvent.setRawPayload( dto.getRawPayload() );
        cityEvent.setCreatedAt( dto.getCreatedAt() );
        cityEvent.setUpdatedAt( dto.getUpdatedAt() );

        return cityEvent;
    }

    @Override
    public CityEventDTO toDto(CityEvent entity) {
        if ( entity == null ) {
            return null;
        }

        CityEventDTO cityEventDTO = new CityEventDTO();

        cityEventDTO.setId( entity.getId() );
        cityEventDTO.setTitle( entity.getTitle() );
        cityEventDTO.setDescription( entity.getDescription() );
        cityEventDTO.setCategory( entity.getCategory() );
        cityEventDTO.setCity( entity.getCity() );
        cityEventDTO.setVenueName( entity.getVenueName() );
        cityEventDTO.setAddressText( entity.getAddressText() );
        cityEventDTO.setStartsAt( entity.getStartsAt() );
        cityEventDTO.setEndsAt( entity.getEndsAt() );
        cityEventDTO.setCoverImageUrl( entity.getCoverImageUrl() );
        cityEventDTO.setPriceLabel( entity.getPriceLabel() );
        cityEventDTO.setSourceName( entity.getSourceName() );
        cityEventDTO.setSourceUrl( entity.getSourceUrl() );
        cityEventDTO.setTicketUrl( entity.getTicketUrl() );
        cityEventDTO.setStatus( entity.getStatus() );
        cityEventDTO.setProviderCode( entity.getProviderCode() );
        cityEventDTO.setExternalId( entity.getExternalId() );
        cityEventDTO.setLastSyncedAt( entity.getLastSyncedAt() );
        cityEventDTO.setRawPayload( entity.getRawPayload() );
        cityEventDTO.setCreatedAt( entity.getCreatedAt() );
        cityEventDTO.setUpdatedAt( entity.getUpdatedAt() );

        return cityEventDTO;
    }

    @Override
    public List<CityEvent> toEntity(List<CityEventDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<CityEvent> list = new ArrayList<CityEvent>( dtoList.size() );
        for ( CityEventDTO cityEventDTO : dtoList ) {
            list.add( toEntity( cityEventDTO ) );
        }

        return list;
    }

    @Override
    public List<CityEventDTO> toDto(List<CityEvent> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CityEventDTO> list = new ArrayList<CityEventDTO>( entityList.size() );
        for ( CityEvent cityEvent : entityList ) {
            list.add( toDto( cityEvent ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(CityEvent entity, CityEventDTO dto) {
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
        if ( dto.getCategory() != null ) {
            entity.setCategory( dto.getCategory() );
        }
        if ( dto.getCity() != null ) {
            entity.setCity( dto.getCity() );
        }
        if ( dto.getVenueName() != null ) {
            entity.setVenueName( dto.getVenueName() );
        }
        if ( dto.getAddressText() != null ) {
            entity.setAddressText( dto.getAddressText() );
        }
        if ( dto.getStartsAt() != null ) {
            entity.setStartsAt( dto.getStartsAt() );
        }
        if ( dto.getEndsAt() != null ) {
            entity.setEndsAt( dto.getEndsAt() );
        }
        if ( dto.getCoverImageUrl() != null ) {
            entity.setCoverImageUrl( dto.getCoverImageUrl() );
        }
        if ( dto.getPriceLabel() != null ) {
            entity.setPriceLabel( dto.getPriceLabel() );
        }
        if ( dto.getSourceName() != null ) {
            entity.setSourceName( dto.getSourceName() );
        }
        if ( dto.getSourceUrl() != null ) {
            entity.setSourceUrl( dto.getSourceUrl() );
        }
        if ( dto.getTicketUrl() != null ) {
            entity.setTicketUrl( dto.getTicketUrl() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getProviderCode() != null ) {
            entity.setProviderCode( dto.getProviderCode() );
        }
        if ( dto.getExternalId() != null ) {
            entity.setExternalId( dto.getExternalId() );
        }
        if ( dto.getLastSyncedAt() != null ) {
            entity.setLastSyncedAt( dto.getLastSyncedAt() );
        }
        if ( dto.getRawPayload() != null ) {
            entity.setRawPayload( dto.getRawPayload() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
    }
}
