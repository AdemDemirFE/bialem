package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.domain.CityEventTicketOffer;
import com.bialem.backend.service.dto.CityEventDTO;
import com.bialem.backend.service.dto.CityEventTicketOfferDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T19:18:39+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CityEventTicketOfferMapperImpl implements CityEventTicketOfferMapper {

    @Override
    public CityEventTicketOffer toEntity(CityEventTicketOfferDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CityEventTicketOffer cityEventTicketOffer = new CityEventTicketOffer();

        cityEventTicketOffer.setId( dto.getId() );
        cityEventTicketOffer.setProviderCode( dto.getProviderCode() );
        cityEventTicketOffer.setExternalOfferId( dto.getExternalOfferId() );
        cityEventTicketOffer.setSellerName( dto.getSellerName() );
        cityEventTicketOffer.setPurchaseUrl( dto.getPurchaseUrl() );
        cityEventTicketOffer.setCurrency( dto.getCurrency() );
        cityEventTicketOffer.setMinPrice( dto.getMinPrice() );
        cityEventTicketOffer.setMaxPrice( dto.getMaxPrice() );
        cityEventTicketOffer.setPriceLabel( dto.getPriceLabel() );
        cityEventTicketOffer.setAvailability( dto.getAvailability() );
        cityEventTicketOffer.setFeesIncluded( dto.getFeesIncluded() );
        cityEventTicketOffer.setIsOfficial( dto.getIsOfficial() );
        cityEventTicketOffer.setLastCheckedAt( dto.getLastCheckedAt() );
        cityEventTicketOffer.setRawPayload( dto.getRawPayload() );
        cityEventTicketOffer.setCreatedAt( dto.getCreatedAt() );
        cityEventTicketOffer.setUpdatedAt( dto.getUpdatedAt() );
        cityEventTicketOffer.cityEvent( cityEventDTOToCityEvent( dto.getCityEvent() ) );

        return cityEventTicketOffer;
    }

    @Override
    public List<CityEventTicketOffer> toEntity(List<CityEventTicketOfferDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<CityEventTicketOffer> list = new ArrayList<CityEventTicketOffer>( dtoList.size() );
        for ( CityEventTicketOfferDTO cityEventTicketOfferDTO : dtoList ) {
            list.add( toEntity( cityEventTicketOfferDTO ) );
        }

        return list;
    }

    @Override
    public List<CityEventTicketOfferDTO> toDto(List<CityEventTicketOffer> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CityEventTicketOfferDTO> list = new ArrayList<CityEventTicketOfferDTO>( entityList.size() );
        for ( CityEventTicketOffer cityEventTicketOffer : entityList ) {
            list.add( toDto( cityEventTicketOffer ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(CityEventTicketOffer entity, CityEventTicketOfferDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getProviderCode() != null ) {
            entity.setProviderCode( dto.getProviderCode() );
        }
        if ( dto.getExternalOfferId() != null ) {
            entity.setExternalOfferId( dto.getExternalOfferId() );
        }
        if ( dto.getSellerName() != null ) {
            entity.setSellerName( dto.getSellerName() );
        }
        if ( dto.getPurchaseUrl() != null ) {
            entity.setPurchaseUrl( dto.getPurchaseUrl() );
        }
        if ( dto.getCurrency() != null ) {
            entity.setCurrency( dto.getCurrency() );
        }
        if ( dto.getMinPrice() != null ) {
            entity.setMinPrice( dto.getMinPrice() );
        }
        if ( dto.getMaxPrice() != null ) {
            entity.setMaxPrice( dto.getMaxPrice() );
        }
        if ( dto.getPriceLabel() != null ) {
            entity.setPriceLabel( dto.getPriceLabel() );
        }
        if ( dto.getAvailability() != null ) {
            entity.setAvailability( dto.getAvailability() );
        }
        if ( dto.getFeesIncluded() != null ) {
            entity.setFeesIncluded( dto.getFeesIncluded() );
        }
        if ( dto.getIsOfficial() != null ) {
            entity.setIsOfficial( dto.getIsOfficial() );
        }
        if ( dto.getLastCheckedAt() != null ) {
            entity.setLastCheckedAt( dto.getLastCheckedAt() );
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
        if ( dto.getCityEvent() != null ) {
            if ( entity.getCityEvent() == null ) {
                entity.cityEvent( new CityEvent() );
            }
            cityEventDTOToCityEvent1( dto.getCityEvent(), entity.getCityEvent() );
        }
    }

    @Override
    public CityEventTicketOfferDTO toDto(CityEventTicketOffer s) {
        if ( s == null ) {
            return null;
        }

        CityEventTicketOfferDTO cityEventTicketOfferDTO = new CityEventTicketOfferDTO();

        cityEventTicketOfferDTO.setCityEvent( toDtoCityEventId( s.getCityEvent() ) );
        cityEventTicketOfferDTO.setId( s.getId() );
        cityEventTicketOfferDTO.setProviderCode( s.getProviderCode() );
        cityEventTicketOfferDTO.setExternalOfferId( s.getExternalOfferId() );
        cityEventTicketOfferDTO.setSellerName( s.getSellerName() );
        cityEventTicketOfferDTO.setPurchaseUrl( s.getPurchaseUrl() );
        cityEventTicketOfferDTO.setCurrency( s.getCurrency() );
        cityEventTicketOfferDTO.setMinPrice( s.getMinPrice() );
        cityEventTicketOfferDTO.setMaxPrice( s.getMaxPrice() );
        cityEventTicketOfferDTO.setPriceLabel( s.getPriceLabel() );
        cityEventTicketOfferDTO.setAvailability( s.getAvailability() );
        cityEventTicketOfferDTO.setFeesIncluded( s.getFeesIncluded() );
        cityEventTicketOfferDTO.setIsOfficial( s.getIsOfficial() );
        cityEventTicketOfferDTO.setLastCheckedAt( s.getLastCheckedAt() );
        cityEventTicketOfferDTO.setRawPayload( s.getRawPayload() );
        cityEventTicketOfferDTO.setCreatedAt( s.getCreatedAt() );
        cityEventTicketOfferDTO.setUpdatedAt( s.getUpdatedAt() );

        return cityEventTicketOfferDTO;
    }

    @Override
    public CityEventDTO toDtoCityEventId(CityEvent cityEvent) {
        if ( cityEvent == null ) {
            return null;
        }

        CityEventDTO cityEventDTO = new CityEventDTO();

        cityEventDTO.setId( cityEvent.getId() );

        return cityEventDTO;
    }

    protected CityEvent cityEventDTOToCityEvent(CityEventDTO cityEventDTO) {
        if ( cityEventDTO == null ) {
            return null;
        }

        CityEvent cityEvent = new CityEvent();

        cityEvent.setId( cityEventDTO.getId() );
        cityEvent.setTitle( cityEventDTO.getTitle() );
        cityEvent.setDescription( cityEventDTO.getDescription() );
        cityEvent.setCategory( cityEventDTO.getCategory() );
        cityEvent.setCity( cityEventDTO.getCity() );
        cityEvent.setVenueName( cityEventDTO.getVenueName() );
        cityEvent.setAddressText( cityEventDTO.getAddressText() );
        cityEvent.setStartsAt( cityEventDTO.getStartsAt() );
        cityEvent.setEndsAt( cityEventDTO.getEndsAt() );
        cityEvent.setCoverImageUrl( cityEventDTO.getCoverImageUrl() );
        cityEvent.setPriceLabel( cityEventDTO.getPriceLabel() );
        cityEvent.setSourceName( cityEventDTO.getSourceName() );
        cityEvent.setSourceUrl( cityEventDTO.getSourceUrl() );
        cityEvent.setTicketUrl( cityEventDTO.getTicketUrl() );
        cityEvent.setStatus( cityEventDTO.getStatus() );
        cityEvent.setProviderCode( cityEventDTO.getProviderCode() );
        cityEvent.setExternalId( cityEventDTO.getExternalId() );
        cityEvent.setLastSyncedAt( cityEventDTO.getLastSyncedAt() );
        cityEvent.setRawPayload( cityEventDTO.getRawPayload() );
        cityEvent.setCreatedAt( cityEventDTO.getCreatedAt() );
        cityEvent.setUpdatedAt( cityEventDTO.getUpdatedAt() );

        return cityEvent;
    }

    protected void cityEventDTOToCityEvent1(CityEventDTO cityEventDTO, CityEvent mappingTarget) {
        if ( cityEventDTO == null ) {
            return;
        }

        if ( cityEventDTO.getId() != null ) {
            mappingTarget.setId( cityEventDTO.getId() );
        }
        if ( cityEventDTO.getTitle() != null ) {
            mappingTarget.setTitle( cityEventDTO.getTitle() );
        }
        if ( cityEventDTO.getDescription() != null ) {
            mappingTarget.setDescription( cityEventDTO.getDescription() );
        }
        if ( cityEventDTO.getCategory() != null ) {
            mappingTarget.setCategory( cityEventDTO.getCategory() );
        }
        if ( cityEventDTO.getCity() != null ) {
            mappingTarget.setCity( cityEventDTO.getCity() );
        }
        if ( cityEventDTO.getVenueName() != null ) {
            mappingTarget.setVenueName( cityEventDTO.getVenueName() );
        }
        if ( cityEventDTO.getAddressText() != null ) {
            mappingTarget.setAddressText( cityEventDTO.getAddressText() );
        }
        if ( cityEventDTO.getStartsAt() != null ) {
            mappingTarget.setStartsAt( cityEventDTO.getStartsAt() );
        }
        if ( cityEventDTO.getEndsAt() != null ) {
            mappingTarget.setEndsAt( cityEventDTO.getEndsAt() );
        }
        if ( cityEventDTO.getCoverImageUrl() != null ) {
            mappingTarget.setCoverImageUrl( cityEventDTO.getCoverImageUrl() );
        }
        if ( cityEventDTO.getPriceLabel() != null ) {
            mappingTarget.setPriceLabel( cityEventDTO.getPriceLabel() );
        }
        if ( cityEventDTO.getSourceName() != null ) {
            mappingTarget.setSourceName( cityEventDTO.getSourceName() );
        }
        if ( cityEventDTO.getSourceUrl() != null ) {
            mappingTarget.setSourceUrl( cityEventDTO.getSourceUrl() );
        }
        if ( cityEventDTO.getTicketUrl() != null ) {
            mappingTarget.setTicketUrl( cityEventDTO.getTicketUrl() );
        }
        if ( cityEventDTO.getStatus() != null ) {
            mappingTarget.setStatus( cityEventDTO.getStatus() );
        }
        if ( cityEventDTO.getProviderCode() != null ) {
            mappingTarget.setProviderCode( cityEventDTO.getProviderCode() );
        }
        if ( cityEventDTO.getExternalId() != null ) {
            mappingTarget.setExternalId( cityEventDTO.getExternalId() );
        }
        if ( cityEventDTO.getLastSyncedAt() != null ) {
            mappingTarget.setLastSyncedAt( cityEventDTO.getLastSyncedAt() );
        }
        if ( cityEventDTO.getRawPayload() != null ) {
            mappingTarget.setRawPayload( cityEventDTO.getRawPayload() );
        }
        if ( cityEventDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( cityEventDTO.getCreatedAt() );
        }
        if ( cityEventDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( cityEventDTO.getUpdatedAt() );
        }
    }
}
