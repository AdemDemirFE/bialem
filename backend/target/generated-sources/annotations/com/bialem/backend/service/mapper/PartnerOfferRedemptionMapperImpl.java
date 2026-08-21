package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerOffer;
import com.bialem.backend.domain.PartnerOfferRedemption;
import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.PartnerOfferDTO;
import com.bialem.backend.service.dto.PartnerOfferRedemptionDTO;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T15:24:54+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PartnerOfferRedemptionMapperImpl implements PartnerOfferRedemptionMapper {

    @Override
    public PartnerOfferRedemption toEntity(PartnerOfferRedemptionDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PartnerOfferRedemption partnerOfferRedemption = new PartnerOfferRedemption();

        partnerOfferRedemption.setId( dto.getId() );
        partnerOfferRedemption.setToken( dto.getToken() );
        partnerOfferRedemption.setRedemptionCode( dto.getRedemptionCode() );
        partnerOfferRedemption.setStatus( dto.getStatus() );
        partnerOfferRedemption.setIssuedAt( dto.getIssuedAt() );
        partnerOfferRedemption.setExpiresAt( dto.getExpiresAt() );
        partnerOfferRedemption.setRedeemedAt( dto.getRedeemedAt() );
        partnerOfferRedemption.setOrderAmount( dto.getOrderAmount() );
        partnerOfferRedemption.setDiscountAmount( dto.getDiscountAmount() );
        partnerOfferRedemption.offer( partnerOfferDTOToPartnerOffer( dto.getOffer() ) );
        partnerOfferRedemption.venue( partnerVenueDTOToPartnerVenue( dto.getVenue() ) );
        partnerOfferRedemption.user( profileDTOToProfile( dto.getUser() ) );
        partnerOfferRedemption.redeemedBy( profileDTOToProfile( dto.getRedeemedBy() ) );

        return partnerOfferRedemption;
    }

    @Override
    public List<PartnerOfferRedemption> toEntity(List<PartnerOfferRedemptionDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PartnerOfferRedemption> list = new ArrayList<PartnerOfferRedemption>( dtoList.size() );
        for ( PartnerOfferRedemptionDTO partnerOfferRedemptionDTO : dtoList ) {
            list.add( toEntity( partnerOfferRedemptionDTO ) );
        }

        return list;
    }

    @Override
    public List<PartnerOfferRedemptionDTO> toDto(List<PartnerOfferRedemption> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PartnerOfferRedemptionDTO> list = new ArrayList<PartnerOfferRedemptionDTO>( entityList.size() );
        for ( PartnerOfferRedemption partnerOfferRedemption : entityList ) {
            list.add( toDto( partnerOfferRedemption ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PartnerOfferRedemption entity, PartnerOfferRedemptionDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getToken() != null ) {
            entity.setToken( dto.getToken() );
        }
        if ( dto.getRedemptionCode() != null ) {
            entity.setRedemptionCode( dto.getRedemptionCode() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getIssuedAt() != null ) {
            entity.setIssuedAt( dto.getIssuedAt() );
        }
        if ( dto.getExpiresAt() != null ) {
            entity.setExpiresAt( dto.getExpiresAt() );
        }
        if ( dto.getRedeemedAt() != null ) {
            entity.setRedeemedAt( dto.getRedeemedAt() );
        }
        if ( dto.getOrderAmount() != null ) {
            entity.setOrderAmount( dto.getOrderAmount() );
        }
        if ( dto.getDiscountAmount() != null ) {
            entity.setDiscountAmount( dto.getDiscountAmount() );
        }
        if ( dto.getOffer() != null ) {
            if ( entity.getOffer() == null ) {
                entity.offer( new PartnerOffer() );
            }
            partnerOfferDTOToPartnerOffer1( dto.getOffer(), entity.getOffer() );
        }
        if ( dto.getVenue() != null ) {
            if ( entity.getVenue() == null ) {
                entity.venue( new PartnerVenue() );
            }
            partnerVenueDTOToPartnerVenue1( dto.getVenue(), entity.getVenue() );
        }
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new Profile() );
            }
            profileDTOToProfile1( dto.getUser(), entity.getUser() );
        }
        if ( dto.getRedeemedBy() != null ) {
            if ( entity.getRedeemedBy() == null ) {
                entity.redeemedBy( new Profile() );
            }
            profileDTOToProfile1( dto.getRedeemedBy(), entity.getRedeemedBy() );
        }
    }

    @Override
    public PartnerOfferRedemptionDTO toDto(PartnerOfferRedemption s) {
        if ( s == null ) {
            return null;
        }

        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = new PartnerOfferRedemptionDTO();

        partnerOfferRedemptionDTO.setOffer( toDtoPartnerOfferId( s.getOffer() ) );
        partnerOfferRedemptionDTO.setVenue( toDtoPartnerVenueId( s.getVenue() ) );
        partnerOfferRedemptionDTO.setUser( toDtoProfileId( s.getUser() ) );
        partnerOfferRedemptionDTO.setRedeemedBy( toDtoProfileId( s.getRedeemedBy() ) );
        partnerOfferRedemptionDTO.setId( s.getId() );
        partnerOfferRedemptionDTO.setToken( s.getToken() );
        partnerOfferRedemptionDTO.setRedemptionCode( s.getRedemptionCode() );
        partnerOfferRedemptionDTO.setStatus( s.getStatus() );
        partnerOfferRedemptionDTO.setIssuedAt( s.getIssuedAt() );
        partnerOfferRedemptionDTO.setExpiresAt( s.getExpiresAt() );
        partnerOfferRedemptionDTO.setRedeemedAt( s.getRedeemedAt() );
        partnerOfferRedemptionDTO.setOrderAmount( s.getOrderAmount() );
        partnerOfferRedemptionDTO.setDiscountAmount( s.getDiscountAmount() );

        return partnerOfferRedemptionDTO;
    }

    @Override
    public PartnerOfferDTO toDtoPartnerOfferId(PartnerOffer partnerOffer) {
        if ( partnerOffer == null ) {
            return null;
        }

        PartnerOfferDTO partnerOfferDTO = new PartnerOfferDTO();

        partnerOfferDTO.setId( partnerOffer.getId() );

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

    @Override
    public ProfileDTO toDtoProfileId(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setId( profile.getId() );

        return profileDTO;
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

    protected PartnerOffer partnerOfferDTOToPartnerOffer(PartnerOfferDTO partnerOfferDTO) {
        if ( partnerOfferDTO == null ) {
            return null;
        }

        PartnerOffer partnerOffer = new PartnerOffer();

        partnerOffer.setId( partnerOfferDTO.getId() );
        partnerOffer.setTitle( partnerOfferDTO.getTitle() );
        partnerOffer.setDescription( partnerOfferDTO.getDescription() );
        partnerOffer.setDiscountPercent( partnerOfferDTO.getDiscountPercent() );
        partnerOffer.setMinimumSpend( partnerOfferDTO.getMinimumSpend() );
        partnerOffer.setMaximumDiscount( partnerOfferDTO.getMaximumDiscount() );
        partnerOffer.setValidFrom( partnerOfferDTO.getValidFrom() );
        partnerOffer.setValidUntil( partnerOfferDTO.getValidUntil() );
        partnerOffer.setValidDays( partnerOfferDTO.getValidDays() );
        partnerOffer.setDailyStartTime( partnerOfferDTO.getDailyStartTime() );
        partnerOffer.setDailyEndTime( partnerOfferDTO.getDailyEndTime() );
        partnerOffer.setPerUserLimit( partnerOfferDTO.getPerUserLimit() );
        partnerOffer.setTerms( partnerOfferDTO.getTerms() );
        partnerOffer.setIsActive( partnerOfferDTO.getIsActive() );
        partnerOffer.setCreatedAt( partnerOfferDTO.getCreatedAt() );
        partnerOffer.setUpdatedAt( partnerOfferDTO.getUpdatedAt() );
        partnerOffer.venue( partnerVenueDTOToPartnerVenue( partnerOfferDTO.getVenue() ) );

        return partnerOffer;
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

    protected void partnerOfferDTOToPartnerOffer1(PartnerOfferDTO partnerOfferDTO, PartnerOffer mappingTarget) {
        if ( partnerOfferDTO == null ) {
            return;
        }

        if ( partnerOfferDTO.getId() != null ) {
            mappingTarget.setId( partnerOfferDTO.getId() );
        }
        if ( partnerOfferDTO.getTitle() != null ) {
            mappingTarget.setTitle( partnerOfferDTO.getTitle() );
        }
        if ( partnerOfferDTO.getDescription() != null ) {
            mappingTarget.setDescription( partnerOfferDTO.getDescription() );
        }
        if ( partnerOfferDTO.getDiscountPercent() != null ) {
            mappingTarget.setDiscountPercent( partnerOfferDTO.getDiscountPercent() );
        }
        if ( partnerOfferDTO.getMinimumSpend() != null ) {
            mappingTarget.setMinimumSpend( partnerOfferDTO.getMinimumSpend() );
        }
        if ( partnerOfferDTO.getMaximumDiscount() != null ) {
            mappingTarget.setMaximumDiscount( partnerOfferDTO.getMaximumDiscount() );
        }
        if ( partnerOfferDTO.getValidFrom() != null ) {
            mappingTarget.setValidFrom( partnerOfferDTO.getValidFrom() );
        }
        if ( partnerOfferDTO.getValidUntil() != null ) {
            mappingTarget.setValidUntil( partnerOfferDTO.getValidUntil() );
        }
        if ( partnerOfferDTO.getValidDays() != null ) {
            mappingTarget.setValidDays( partnerOfferDTO.getValidDays() );
        }
        if ( partnerOfferDTO.getDailyStartTime() != null ) {
            mappingTarget.setDailyStartTime( partnerOfferDTO.getDailyStartTime() );
        }
        if ( partnerOfferDTO.getDailyEndTime() != null ) {
            mappingTarget.setDailyEndTime( partnerOfferDTO.getDailyEndTime() );
        }
        if ( partnerOfferDTO.getPerUserLimit() != null ) {
            mappingTarget.setPerUserLimit( partnerOfferDTO.getPerUserLimit() );
        }
        if ( partnerOfferDTO.getTerms() != null ) {
            mappingTarget.setTerms( partnerOfferDTO.getTerms() );
        }
        if ( partnerOfferDTO.getIsActive() != null ) {
            mappingTarget.setIsActive( partnerOfferDTO.getIsActive() );
        }
        if ( partnerOfferDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( partnerOfferDTO.getCreatedAt() );
        }
        if ( partnerOfferDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( partnerOfferDTO.getUpdatedAt() );
        }
        if ( partnerOfferDTO.getVenue() != null ) {
            if ( mappingTarget.getVenue() == null ) {
                mappingTarget.venue( new PartnerVenue() );
            }
            partnerVenueDTOToPartnerVenue1( partnerOfferDTO.getVenue(), mappingTarget.getVenue() );
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
