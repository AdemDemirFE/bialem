package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PartnerVenue;
import com.bialem.backend.domain.PartnerVenueStaff;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.PartnerVenueDTO;
import com.bialem.backend.service.dto.PartnerVenueStaffDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T09:18:30+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PartnerVenueStaffMapperImpl implements PartnerVenueStaffMapper {

    @Override
    public PartnerVenueStaff toEntity(PartnerVenueStaffDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PartnerVenueStaff partnerVenueStaff = new PartnerVenueStaff();

        partnerVenueStaff.setId( dto.getId() );
        partnerVenueStaff.setIsActive( dto.getIsActive() );
        partnerVenueStaff.setCreatedAt( dto.getCreatedAt() );
        partnerVenueStaff.venue( partnerVenueDTOToPartnerVenue( dto.getVenue() ) );
        partnerVenueStaff.user( profileDTOToProfile( dto.getUser() ) );

        return partnerVenueStaff;
    }

    @Override
    public List<PartnerVenueStaff> toEntity(List<PartnerVenueStaffDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PartnerVenueStaff> list = new ArrayList<PartnerVenueStaff>( dtoList.size() );
        for ( PartnerVenueStaffDTO partnerVenueStaffDTO : dtoList ) {
            list.add( toEntity( partnerVenueStaffDTO ) );
        }

        return list;
    }

    @Override
    public List<PartnerVenueStaffDTO> toDto(List<PartnerVenueStaff> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PartnerVenueStaffDTO> list = new ArrayList<PartnerVenueStaffDTO>( entityList.size() );
        for ( PartnerVenueStaff partnerVenueStaff : entityList ) {
            list.add( toDto( partnerVenueStaff ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PartnerVenueStaff entity, PartnerVenueStaffDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
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
    }

    @Override
    public PartnerVenueStaffDTO toDto(PartnerVenueStaff s) {
        if ( s == null ) {
            return null;
        }

        PartnerVenueStaffDTO partnerVenueStaffDTO = new PartnerVenueStaffDTO();

        partnerVenueStaffDTO.setVenue( toDtoPartnerVenueId( s.getVenue() ) );
        partnerVenueStaffDTO.setUser( toDtoProfileId( s.getUser() ) );
        partnerVenueStaffDTO.setId( s.getId() );
        partnerVenueStaffDTO.setIsActive( s.getIsActive() );
        partnerVenueStaffDTO.setCreatedAt( s.getCreatedAt() );

        return partnerVenueStaffDTO;
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
