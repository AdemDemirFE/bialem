package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.CommunityMemberDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T09:18:29+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CommunityMemberMapperImpl implements CommunityMemberMapper {

    @Override
    public CommunityMember toEntity(CommunityMemberDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CommunityMember communityMember = new CommunityMember();

        communityMember.setId( dto.getId() );
        communityMember.setRole( dto.getRole() );
        communityMember.setStatus( dto.getStatus() );
        communityMember.setCreatedAt( dto.getCreatedAt() );
        communityMember.community( communityDTOToCommunity( dto.getCommunity() ) );
        communityMember.user( profileDTOToProfile( dto.getUser() ) );

        return communityMember;
    }

    @Override
    public List<CommunityMember> toEntity(List<CommunityMemberDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<CommunityMember> list = new ArrayList<CommunityMember>( dtoList.size() );
        for ( CommunityMemberDTO communityMemberDTO : dtoList ) {
            list.add( toEntity( communityMemberDTO ) );
        }

        return list;
    }

    @Override
    public List<CommunityMemberDTO> toDto(List<CommunityMember> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CommunityMemberDTO> list = new ArrayList<CommunityMemberDTO>( entityList.size() );
        for ( CommunityMember communityMember : entityList ) {
            list.add( toDto( communityMember ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(CommunityMember entity, CommunityMemberDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getRole() != null ) {
            entity.setRole( dto.getRole() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getCommunity() != null ) {
            if ( entity.getCommunity() == null ) {
                entity.community( new Community() );
            }
            communityDTOToCommunity1( dto.getCommunity(), entity.getCommunity() );
        }
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new Profile() );
            }
            profileDTOToProfile1( dto.getUser(), entity.getUser() );
        }
    }

    @Override
    public CommunityMemberDTO toDto(CommunityMember s) {
        if ( s == null ) {
            return null;
        }

        CommunityMemberDTO communityMemberDTO = new CommunityMemberDTO();

        communityMemberDTO.setCommunity( toDtoCommunityId( s.getCommunity() ) );
        communityMemberDTO.setUser( toDtoProfileId( s.getUser() ) );
        communityMemberDTO.setId( s.getId() );
        communityMemberDTO.setRole( s.getRole() );
        communityMemberDTO.setStatus( s.getStatus() );
        communityMemberDTO.setCreatedAt( s.getCreatedAt() );

        return communityMemberDTO;
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
}
