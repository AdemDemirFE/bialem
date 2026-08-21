package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.FollowRequest;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.FollowRequestDTO;
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
public class FollowRequestMapperImpl implements FollowRequestMapper {

    @Override
    public FollowRequest toEntity(FollowRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        FollowRequest followRequest = new FollowRequest();

        followRequest.setId( dto.getId() );
        followRequest.setCreatedAt( dto.getCreatedAt() );
        followRequest.requester( profileDTOToProfile( dto.getRequester() ) );
        followRequest.targetUser( profileDTOToProfile( dto.getTargetUser() ) );

        return followRequest;
    }

    @Override
    public List<FollowRequest> toEntity(List<FollowRequestDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<FollowRequest> list = new ArrayList<FollowRequest>( dtoList.size() );
        for ( FollowRequestDTO followRequestDTO : dtoList ) {
            list.add( toEntity( followRequestDTO ) );
        }

        return list;
    }

    @Override
    public List<FollowRequestDTO> toDto(List<FollowRequest> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<FollowRequestDTO> list = new ArrayList<FollowRequestDTO>( entityList.size() );
        for ( FollowRequest followRequest : entityList ) {
            list.add( toDto( followRequest ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(FollowRequest entity, FollowRequestDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getRequester() != null ) {
            if ( entity.getRequester() == null ) {
                entity.requester( new Profile() );
            }
            profileDTOToProfile1( dto.getRequester(), entity.getRequester() );
        }
        if ( dto.getTargetUser() != null ) {
            if ( entity.getTargetUser() == null ) {
                entity.targetUser( new Profile() );
            }
            profileDTOToProfile1( dto.getTargetUser(), entity.getTargetUser() );
        }
    }

    @Override
    public FollowRequestDTO toDto(FollowRequest s) {
        if ( s == null ) {
            return null;
        }

        FollowRequestDTO followRequestDTO = new FollowRequestDTO();

        followRequestDTO.setRequester( toDtoProfileId( s.getRequester() ) );
        followRequestDTO.setTargetUser( toDtoProfileId( s.getTargetUser() ) );
        followRequestDTO.setId( s.getId() );
        followRequestDTO.setCreatedAt( s.getCreatedAt() );

        return followRequestDTO;
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
