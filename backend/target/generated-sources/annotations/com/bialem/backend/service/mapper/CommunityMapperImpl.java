package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T22:10:22+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CommunityMapperImpl implements CommunityMapper {

    @Override
    public Community toEntity(CommunityDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Community community = new Community();

        community.setId( dto.getId() );
        community.setName( dto.getName() );
        community.setSlug( dto.getSlug() );
        community.setDescription( dto.getDescription() );
        community.setVisibility( dto.getVisibility() );
        community.setCoverImageUrl( dto.getCoverImageUrl() );
        community.setCommunityType( dto.getCommunityType() );
        community.setPartnerTrustLevel( dto.getPartnerTrustLevel() );
        community.setIsVerifiedPartner( dto.getIsVerifiedPartner() );
        community.setIsDiscoverable( dto.getIsDiscoverable() );
        community.setCreatedAt( dto.getCreatedAt() );
        community.setUpdatedAt( dto.getUpdatedAt() );
        community.parent( toEntity( dto.getParent() ) );
        community.categoryHub( toEntity( dto.getCategoryHub() ) );
        community.createdBy( profileDTOToProfile( dto.getCreatedBy() ) );
        community.leadModerator( profileDTOToProfile( dto.getLeadModerator() ) );

        return community;
    }

    @Override
    public List<Community> toEntity(List<CommunityDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Community> list = new ArrayList<Community>( dtoList.size() );
        for ( CommunityDTO communityDTO : dtoList ) {
            list.add( toEntity( communityDTO ) );
        }

        return list;
    }

    @Override
    public List<CommunityDTO> toDto(List<Community> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CommunityDTO> list = new ArrayList<CommunityDTO>( entityList.size() );
        for ( Community community : entityList ) {
            list.add( toDto( community ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Community entity, CommunityDTO dto) {
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
        if ( dto.getVisibility() != null ) {
            entity.setVisibility( dto.getVisibility() );
        }
        if ( dto.getCoverImageUrl() != null ) {
            entity.setCoverImageUrl( dto.getCoverImageUrl() );
        }
        if ( dto.getCommunityType() != null ) {
            entity.setCommunityType( dto.getCommunityType() );
        }
        if ( dto.getPartnerTrustLevel() != null ) {
            entity.setPartnerTrustLevel( dto.getPartnerTrustLevel() );
        }
        if ( dto.getIsVerifiedPartner() != null ) {
            entity.setIsVerifiedPartner( dto.getIsVerifiedPartner() );
        }
        if ( dto.getIsDiscoverable() != null ) {
            entity.setIsDiscoverable( dto.getIsDiscoverable() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getParent() != null ) {
            entity.parent( toEntity( dto.getParent() ) );
        }
        if ( dto.getCategoryHub() != null ) {
            entity.categoryHub( toEntity( dto.getCategoryHub() ) );
        }
        if ( dto.getCreatedBy() != null ) {
            if ( entity.getCreatedBy() == null ) {
                entity.createdBy( new Profile() );
            }
            profileDTOToProfile1( dto.getCreatedBy(), entity.getCreatedBy() );
        }
        if ( dto.getLeadModerator() != null ) {
            if ( entity.getLeadModerator() == null ) {
                entity.leadModerator( new Profile() );
            }
            profileDTOToProfile1( dto.getLeadModerator(), entity.getLeadModerator() );
        }
    }

    @Override
    public CommunityDTO toDto(Community s) {
        if ( s == null ) {
            return null;
        }

        CommunityDTO communityDTO = new CommunityDTO();

        communityDTO.setParent( toDtoCommunityId( s.getParent() ) );
        communityDTO.setCategoryHub( toDtoCommunityId( s.getCategoryHub() ) );
        communityDTO.setCreatedBy( toDtoProfileId( s.getCreatedBy() ) );
        communityDTO.setLeadModerator( toDtoProfileId( s.getLeadModerator() ) );
        communityDTO.setId( s.getId() );
        communityDTO.setName( s.getName() );
        communityDTO.setSlug( s.getSlug() );
        communityDTO.setDescription( s.getDescription() );
        communityDTO.setVisibility( s.getVisibility() );
        communityDTO.setCoverImageUrl( s.getCoverImageUrl() );
        communityDTO.setCommunityType( s.getCommunityType() );
        communityDTO.setPartnerTrustLevel( s.getPartnerTrustLevel() );
        communityDTO.setIsVerifiedPartner( s.getIsVerifiedPartner() );
        communityDTO.setIsDiscoverable( s.getIsDiscoverable() );
        communityDTO.setCreatedAt( s.getCreatedAt() );
        communityDTO.setUpdatedAt( s.getUpdatedAt() );

        return communityDTO;
    }

    @Override
    public CommunityDTO toDtoCommunityId(Community community) {
        if ( community == null ) {
            return null;
        }

        CommunityDTO communityDTO = new CommunityDTO();

        communityDTO.setId( community.getId() );

        return communityDTO;
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
