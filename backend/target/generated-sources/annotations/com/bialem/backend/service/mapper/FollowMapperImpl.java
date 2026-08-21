package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Follow;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.FollowDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T15:24:45+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class FollowMapperImpl implements FollowMapper {

    @Override
    public Follow toEntity(FollowDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Follow follow = new Follow();

        follow.setId( dto.getId() );
        follow.setCreatedAt( dto.getCreatedAt() );
        follow.follower( profileDTOToProfile( dto.getFollower() ) );
        follow.followed( profileDTOToProfile( dto.getFollowed() ) );

        return follow;
    }

    @Override
    public List<Follow> toEntity(List<FollowDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Follow> list = new ArrayList<Follow>( dtoList.size() );
        for ( FollowDTO followDTO : dtoList ) {
            list.add( toEntity( followDTO ) );
        }

        return list;
    }

    @Override
    public List<FollowDTO> toDto(List<Follow> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<FollowDTO> list = new ArrayList<FollowDTO>( entityList.size() );
        for ( Follow follow : entityList ) {
            list.add( toDto( follow ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Follow entity, FollowDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getFollower() != null ) {
            if ( entity.getFollower() == null ) {
                entity.follower( new Profile() );
            }
            profileDTOToProfile1( dto.getFollower(), entity.getFollower() );
        }
        if ( dto.getFollowed() != null ) {
            if ( entity.getFollowed() == null ) {
                entity.followed( new Profile() );
            }
            profileDTOToProfile1( dto.getFollowed(), entity.getFollowed() );
        }
    }

    @Override
    public FollowDTO toDto(Follow s) {
        if ( s == null ) {
            return null;
        }

        FollowDTO followDTO = new FollowDTO();

        followDTO.setFollower( toDtoProfileId( s.getFollower() ) );
        followDTO.setFollowed( toDtoProfileId( s.getFollowed() ) );
        followDTO.setId( s.getId() );
        followDTO.setCreatedAt( s.getCreatedAt() );

        return followDTO;
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
