package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.HonorBadge;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.UserHonorBadge;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.HonorBadgeDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import com.bialem.backend.service.dto.UserHonorBadgeDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T15:24:49+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserHonorBadgeMapperImpl implements UserHonorBadgeMapper {

    @Override
    public UserHonorBadge toEntity(UserHonorBadgeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserHonorBadge userHonorBadge = new UserHonorBadge();

        userHonorBadge.setId( dto.getId() );
        userHonorBadge.setReason( dto.getReason() );
        userHonorBadge.setAwardedAt( dto.getAwardedAt() );
        userHonorBadge.user( profileDTOToProfile( dto.getUser() ) );
        userHonorBadge.badge( honorBadgeDTOToHonorBadge( dto.getBadge() ) );
        userHonorBadge.awardedBy( profileDTOToProfile( dto.getAwardedBy() ) );

        return userHonorBadge;
    }

    @Override
    public List<UserHonorBadge> toEntity(List<UserHonorBadgeDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<UserHonorBadge> list = new ArrayList<UserHonorBadge>( dtoList.size() );
        for ( UserHonorBadgeDTO userHonorBadgeDTO : dtoList ) {
            list.add( toEntity( userHonorBadgeDTO ) );
        }

        return list;
    }

    @Override
    public List<UserHonorBadgeDTO> toDto(List<UserHonorBadge> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<UserHonorBadgeDTO> list = new ArrayList<UserHonorBadgeDTO>( entityList.size() );
        for ( UserHonorBadge userHonorBadge : entityList ) {
            list.add( toDto( userHonorBadge ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(UserHonorBadge entity, UserHonorBadgeDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getReason() != null ) {
            entity.setReason( dto.getReason() );
        }
        if ( dto.getAwardedAt() != null ) {
            entity.setAwardedAt( dto.getAwardedAt() );
        }
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new Profile() );
            }
            profileDTOToProfile1( dto.getUser(), entity.getUser() );
        }
        if ( dto.getBadge() != null ) {
            if ( entity.getBadge() == null ) {
                entity.badge( new HonorBadge() );
            }
            honorBadgeDTOToHonorBadge1( dto.getBadge(), entity.getBadge() );
        }
        if ( dto.getAwardedBy() != null ) {
            if ( entity.getAwardedBy() == null ) {
                entity.awardedBy( new Profile() );
            }
            profileDTOToProfile1( dto.getAwardedBy(), entity.getAwardedBy() );
        }
    }

    @Override
    public UserHonorBadgeDTO toDto(UserHonorBadge s) {
        if ( s == null ) {
            return null;
        }

        UserHonorBadgeDTO userHonorBadgeDTO = new UserHonorBadgeDTO();

        userHonorBadgeDTO.setUser( toDtoProfileId( s.getUser() ) );
        userHonorBadgeDTO.setBadge( toDtoHonorBadgeId( s.getBadge() ) );
        userHonorBadgeDTO.setAwardedBy( toDtoProfileId( s.getAwardedBy() ) );
        userHonorBadgeDTO.setId( s.getId() );
        userHonorBadgeDTO.setReason( s.getReason() );
        userHonorBadgeDTO.setAwardedAt( s.getAwardedAt() );

        return userHonorBadgeDTO;
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

    @Override
    public HonorBadgeDTO toDtoHonorBadgeId(HonorBadge honorBadge) {
        if ( honorBadge == null ) {
            return null;
        }

        HonorBadgeDTO honorBadgeDTO = new HonorBadgeDTO();

        honorBadgeDTO.setId( honorBadge.getId() );

        return honorBadgeDTO;
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

    protected Community communityDTOToCommunity(CommunityDTO communityDTO) {
        if ( communityDTO == null ) {
            return null;
        }

        Community community = new Community();

        community.setId( communityDTO.getId() );
        community.setName( communityDTO.getName() );
        community.setSlug( communityDTO.getSlug() );
        community.setDescription( communityDTO.getDescription() );
        community.setVisibility( communityDTO.getVisibility() );
        community.setCoverImageUrl( communityDTO.getCoverImageUrl() );
        community.setCommunityType( communityDTO.getCommunityType() );
        community.setPartnerTrustLevel( communityDTO.getPartnerTrustLevel() );
        community.setIsVerifiedPartner( communityDTO.getIsVerifiedPartner() );
        community.setIsDiscoverable( communityDTO.getIsDiscoverable() );
        community.setCreatedAt( communityDTO.getCreatedAt() );
        community.setUpdatedAt( communityDTO.getUpdatedAt() );
        community.parent( communityDTOToCommunity( communityDTO.getParent() ) );
        community.categoryHub( communityDTOToCommunity( communityDTO.getCategoryHub() ) );
        community.createdBy( profileDTOToProfile( communityDTO.getCreatedBy() ) );
        community.leadModerator( profileDTOToProfile( communityDTO.getLeadModerator() ) );

        return community;
    }

    protected HonorBadge honorBadgeDTOToHonorBadge(HonorBadgeDTO honorBadgeDTO) {
        if ( honorBadgeDTO == null ) {
            return null;
        }

        HonorBadge honorBadge = new HonorBadge();

        honorBadge.setId( honorBadgeDTO.getId() );
        honorBadge.setCode( honorBadgeDTO.getCode() );
        honorBadge.setNameTemplate( honorBadgeDTO.getNameTemplate() );
        honorBadge.setDescription( honorBadgeDTO.getDescription() );
        honorBadge.setBadgeType( honorBadgeDTO.getBadgeType() );
        honorBadge.setMinimumCheckIns( honorBadgeDTO.getMinimumCheckIns() );
        honorBadge.setIsActive( honorBadgeDTO.getIsActive() );
        honorBadge.setCreatedAt( honorBadgeDTO.getCreatedAt() );
        honorBadge.community( communityDTOToCommunity( honorBadgeDTO.getCommunity() ) );

        return honorBadge;
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

    protected void communityDTOToCommunity1(CommunityDTO communityDTO, Community mappingTarget) {
        if ( communityDTO == null ) {
            return;
        }

        if ( communityDTO.getId() != null ) {
            mappingTarget.setId( communityDTO.getId() );
        }
        if ( communityDTO.getName() != null ) {
            mappingTarget.setName( communityDTO.getName() );
        }
        if ( communityDTO.getSlug() != null ) {
            mappingTarget.setSlug( communityDTO.getSlug() );
        }
        if ( communityDTO.getDescription() != null ) {
            mappingTarget.setDescription( communityDTO.getDescription() );
        }
        if ( communityDTO.getVisibility() != null ) {
            mappingTarget.setVisibility( communityDTO.getVisibility() );
        }
        if ( communityDTO.getCoverImageUrl() != null ) {
            mappingTarget.setCoverImageUrl( communityDTO.getCoverImageUrl() );
        }
        if ( communityDTO.getCommunityType() != null ) {
            mappingTarget.setCommunityType( communityDTO.getCommunityType() );
        }
        if ( communityDTO.getPartnerTrustLevel() != null ) {
            mappingTarget.setPartnerTrustLevel( communityDTO.getPartnerTrustLevel() );
        }
        if ( communityDTO.getIsVerifiedPartner() != null ) {
            mappingTarget.setIsVerifiedPartner( communityDTO.getIsVerifiedPartner() );
        }
        if ( communityDTO.getIsDiscoverable() != null ) {
            mappingTarget.setIsDiscoverable( communityDTO.getIsDiscoverable() );
        }
        if ( communityDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( communityDTO.getCreatedAt() );
        }
        if ( communityDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( communityDTO.getUpdatedAt() );
        }
        if ( communityDTO.getParent() != null ) {
            if ( mappingTarget.getParent() == null ) {
                mappingTarget.parent( new Community() );
            }
            communityDTOToCommunity1( communityDTO.getParent(), mappingTarget.getParent() );
        }
        if ( communityDTO.getCategoryHub() != null ) {
            if ( mappingTarget.getCategoryHub() == null ) {
                mappingTarget.categoryHub( new Community() );
            }
            communityDTOToCommunity1( communityDTO.getCategoryHub(), mappingTarget.getCategoryHub() );
        }
        if ( communityDTO.getCreatedBy() != null ) {
            if ( mappingTarget.getCreatedBy() == null ) {
                mappingTarget.createdBy( new Profile() );
            }
            profileDTOToProfile1( communityDTO.getCreatedBy(), mappingTarget.getCreatedBy() );
        }
        if ( communityDTO.getLeadModerator() != null ) {
            if ( mappingTarget.getLeadModerator() == null ) {
                mappingTarget.leadModerator( new Profile() );
            }
            profileDTOToProfile1( communityDTO.getLeadModerator(), mappingTarget.getLeadModerator() );
        }
    }

    protected void honorBadgeDTOToHonorBadge1(HonorBadgeDTO honorBadgeDTO, HonorBadge mappingTarget) {
        if ( honorBadgeDTO == null ) {
            return;
        }

        if ( honorBadgeDTO.getId() != null ) {
            mappingTarget.setId( honorBadgeDTO.getId() );
        }
        if ( honorBadgeDTO.getCode() != null ) {
            mappingTarget.setCode( honorBadgeDTO.getCode() );
        }
        if ( honorBadgeDTO.getNameTemplate() != null ) {
            mappingTarget.setNameTemplate( honorBadgeDTO.getNameTemplate() );
        }
        if ( honorBadgeDTO.getDescription() != null ) {
            mappingTarget.setDescription( honorBadgeDTO.getDescription() );
        }
        if ( honorBadgeDTO.getBadgeType() != null ) {
            mappingTarget.setBadgeType( honorBadgeDTO.getBadgeType() );
        }
        if ( honorBadgeDTO.getMinimumCheckIns() != null ) {
            mappingTarget.setMinimumCheckIns( honorBadgeDTO.getMinimumCheckIns() );
        }
        if ( honorBadgeDTO.getIsActive() != null ) {
            mappingTarget.setIsActive( honorBadgeDTO.getIsActive() );
        }
        if ( honorBadgeDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( honorBadgeDTO.getCreatedAt() );
        }
        if ( honorBadgeDTO.getCommunity() != null ) {
            if ( mappingTarget.getCommunity() == null ) {
                mappingTarget.community( new Community() );
            }
            communityDTOToCommunity1( honorBadgeDTO.getCommunity(), mappingTarget.getCommunity() );
        }
    }
}
