package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.domain.CityEventInterest;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CityEventDTO;
import com.bialem.backend.service.dto.CityEventInterestDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T10:10:41+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CityEventInterestMapperImpl implements CityEventInterestMapper {

    @Override
    public CityEventInterest toEntity(CityEventInterestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CityEventInterest cityEventInterest = new CityEventInterest();

        cityEventInterest.setId( dto.getId() );
        cityEventInterest.setLookingForCompany( dto.getLookingForCompany() );
        cityEventInterest.setCreatedAt( dto.getCreatedAt() );
        cityEventInterest.setUpdatedAt( dto.getUpdatedAt() );
        cityEventInterest.cityEvent( cityEventDTOToCityEvent( dto.getCityEvent() ) );
        cityEventInterest.user( profileDTOToProfile( dto.getUser() ) );

        return cityEventInterest;
    }

    @Override
    public List<CityEventInterest> toEntity(List<CityEventInterestDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<CityEventInterest> list = new ArrayList<CityEventInterest>( dtoList.size() );
        for ( CityEventInterestDTO cityEventInterestDTO : dtoList ) {
            list.add( toEntity( cityEventInterestDTO ) );
        }

        return list;
    }

    @Override
    public List<CityEventInterestDTO> toDto(List<CityEventInterest> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CityEventInterestDTO> list = new ArrayList<CityEventInterestDTO>( entityList.size() );
        for ( CityEventInterest cityEventInterest : entityList ) {
            list.add( toDto( cityEventInterest ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(CityEventInterest entity, CityEventInterestDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getLookingForCompany() != null ) {
            entity.setLookingForCompany( dto.getLookingForCompany() );
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
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new Profile() );
            }
            profileDTOToProfile1( dto.getUser(), entity.getUser() );
        }
    }

    @Override
    public CityEventInterestDTO toDto(CityEventInterest s) {
        if ( s == null ) {
            return null;
        }

        CityEventInterestDTO cityEventInterestDTO = new CityEventInterestDTO();

        cityEventInterestDTO.setCityEvent( toDtoCityEventId( s.getCityEvent() ) );
        cityEventInterestDTO.setUser( toDtoProfileId( s.getUser() ) );
        cityEventInterestDTO.setId( s.getId() );
        cityEventInterestDTO.setLookingForCompany( s.getLookingForCompany() );
        cityEventInterestDTO.setCreatedAt( s.getCreatedAt() );
        cityEventInterestDTO.setUpdatedAt( s.getUpdatedAt() );

        return cityEventInterestDTO;
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

    @Override
    public ProfileDTO toDtoProfileId(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setId( profile.getId() );

        return profileDTO;
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

    protected User userDTOToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDTO.getId() );
        user.setLogin( userDTO.getLogin() );

        return user;
    }

    protected Profile profileDTOToProfile(ProfileDTO profileDTO) {
        if ( profileDTO == null ) {
            return null;
        }

        Profile profile = new Profile();

        profile.setId( profileDTO.getId() );
        profile.setDisplayName( profileDTO.getDisplayName() );
        profile.setUsername( profileDTO.getUsername() );
        profile.setAvatarUrl( profileDTO.getAvatarUrl() );
        profile.setBio( profileDTO.getBio() );
        profile.setCity( profileDTO.getCity() );
        profile.setStatus( profileDTO.getStatus() );
        profile.setIsVerified( profileDTO.getIsVerified() );
        profile.setCreatedAt( profileDTO.getCreatedAt() );
        profile.setUpdatedAt( profileDTO.getUpdatedAt() );
        profile.user( userDTOToUser( profileDTO.getUser() ) );

        return profile;
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

    protected void userDTOToUser1(UserDTO userDTO, User mappingTarget) {
        if ( userDTO == null ) {
            return;
        }

        if ( userDTO.getId() != null ) {
            mappingTarget.setId( userDTO.getId() );
        }
        if ( userDTO.getLogin() != null ) {
            mappingTarget.setLogin( userDTO.getLogin() );
        }
    }

    protected void profileDTOToProfile1(ProfileDTO profileDTO, Profile mappingTarget) {
        if ( profileDTO == null ) {
            return;
        }

        if ( profileDTO.getId() != null ) {
            mappingTarget.setId( profileDTO.getId() );
        }
        if ( profileDTO.getDisplayName() != null ) {
            mappingTarget.setDisplayName( profileDTO.getDisplayName() );
        }
        if ( profileDTO.getUsername() != null ) {
            mappingTarget.setUsername( profileDTO.getUsername() );
        }
        if ( profileDTO.getAvatarUrl() != null ) {
            mappingTarget.setAvatarUrl( profileDTO.getAvatarUrl() );
        }
        if ( profileDTO.getBio() != null ) {
            mappingTarget.setBio( profileDTO.getBio() );
        }
        if ( profileDTO.getCity() != null ) {
            mappingTarget.setCity( profileDTO.getCity() );
        }
        if ( profileDTO.getStatus() != null ) {
            mappingTarget.setStatus( profileDTO.getStatus() );
        }
        if ( profileDTO.getIsVerified() != null ) {
            mappingTarget.setIsVerified( profileDTO.getIsVerified() );
        }
        if ( profileDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( profileDTO.getCreatedAt() );
        }
        if ( profileDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( profileDTO.getUpdatedAt() );
        }
        if ( profileDTO.getUser() != null ) {
            if ( mappingTarget.getUser() == null ) {
                mappingTarget.user( new User() );
            }
            userDTOToUser1( profileDTO.getUser(), mappingTarget.getUser() );
        }
    }
}
