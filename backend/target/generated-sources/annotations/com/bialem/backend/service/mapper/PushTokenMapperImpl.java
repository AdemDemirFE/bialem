package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.PushToken;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.PushTokenDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T19:18:42+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PushTokenMapperImpl implements PushTokenMapper {

    @Override
    public PushToken toEntity(PushTokenDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PushToken pushToken = new PushToken();

        pushToken.setId( dto.getId() );
        pushToken.setDeviceToken( dto.getDeviceToken() );
        pushToken.setPlatform( dto.getPlatform() );
        pushToken.setDeviceName( dto.getDeviceName() );
        pushToken.setIsActive( dto.getIsActive() );
        pushToken.setLastSeenAt( dto.getLastSeenAt() );
        pushToken.setCreatedAt( dto.getCreatedAt() );
        pushToken.user( profileDTOToProfile( dto.getUser() ) );

        return pushToken;
    }

    @Override
    public List<PushToken> toEntity(List<PushTokenDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PushToken> list = new ArrayList<PushToken>( dtoList.size() );
        for ( PushTokenDTO pushTokenDTO : dtoList ) {
            list.add( toEntity( pushTokenDTO ) );
        }

        return list;
    }

    @Override
    public List<PushTokenDTO> toDto(List<PushToken> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PushTokenDTO> list = new ArrayList<PushTokenDTO>( entityList.size() );
        for ( PushToken pushToken : entityList ) {
            list.add( toDto( pushToken ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PushToken entity, PushTokenDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getDeviceToken() != null ) {
            entity.setDeviceToken( dto.getDeviceToken() );
        }
        if ( dto.getPlatform() != null ) {
            entity.setPlatform( dto.getPlatform() );
        }
        if ( dto.getDeviceName() != null ) {
            entity.setDeviceName( dto.getDeviceName() );
        }
        if ( dto.getIsActive() != null ) {
            entity.setIsActive( dto.getIsActive() );
        }
        if ( dto.getLastSeenAt() != null ) {
            entity.setLastSeenAt( dto.getLastSeenAt() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new Profile() );
            }
            profileDTOToProfile1( dto.getUser(), entity.getUser() );
        }
    }

    @Override
    public PushTokenDTO toDto(PushToken s) {
        if ( s == null ) {
            return null;
        }

        PushTokenDTO pushTokenDTO = new PushTokenDTO();

        pushTokenDTO.setUser( toDtoProfileId( s.getUser() ) );
        pushTokenDTO.setId( s.getId() );
        pushTokenDTO.setDeviceToken( s.getDeviceToken() );
        pushTokenDTO.setPlatform( s.getPlatform() );
        pushTokenDTO.setDeviceName( s.getDeviceName() );
        pushTokenDTO.setIsActive( s.getIsActive() );
        pushTokenDTO.setLastSeenAt( s.getLastSeenAt() );
        pushTokenDTO.setCreatedAt( s.getCreatedAt() );

        return pushTokenDTO;
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
