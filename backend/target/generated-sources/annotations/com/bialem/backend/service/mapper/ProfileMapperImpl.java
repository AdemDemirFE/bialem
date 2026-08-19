package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T16:30:54+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public Profile toEntity(ProfileDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Profile profile = new Profile();

        profile.setId( dto.getId() );
        profile.setDisplayName( dto.getDisplayName() );
        profile.setUsername( dto.getUsername() );
        profile.setAvatarUrl( dto.getAvatarUrl() );
        profile.setBio( dto.getBio() );
        profile.setCity( dto.getCity() );
        profile.setStatus( dto.getStatus() );
        profile.setIsVerified( dto.getIsVerified() );
        profile.setCreatedAt( dto.getCreatedAt() );
        profile.setUpdatedAt( dto.getUpdatedAt() );
        profile.user( userDTOToUser( dto.getUser() ) );

        return profile;
    }

    @Override
    public List<Profile> toEntity(List<ProfileDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Profile> list = new ArrayList<Profile>( dtoList.size() );
        for ( ProfileDTO profileDTO : dtoList ) {
            list.add( toEntity( profileDTO ) );
        }

        return list;
    }

    @Override
    public List<ProfileDTO> toDto(List<Profile> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ProfileDTO> list = new ArrayList<ProfileDTO>( entityList.size() );
        for ( Profile profile : entityList ) {
            list.add( toDto( profile ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Profile entity, ProfileDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getDisplayName() != null ) {
            entity.setDisplayName( dto.getDisplayName() );
        }
        if ( dto.getUsername() != null ) {
            entity.setUsername( dto.getUsername() );
        }
        if ( dto.getAvatarUrl() != null ) {
            entity.setAvatarUrl( dto.getAvatarUrl() );
        }
        if ( dto.getBio() != null ) {
            entity.setBio( dto.getBio() );
        }
        if ( dto.getCity() != null ) {
            entity.setCity( dto.getCity() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getIsVerified() != null ) {
            entity.setIsVerified( dto.getIsVerified() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new User() );
            }
            userDTOToUser1( dto.getUser(), entity.getUser() );
        }
    }

    @Override
    public ProfileDTO toDto(Profile s) {
        if ( s == null ) {
            return null;
        }

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setUser( toDtoUserLogin( s.getUser() ) );
        profileDTO.setId( s.getId() );
        profileDTO.setDisplayName( s.getDisplayName() );
        profileDTO.setUsername( s.getUsername() );
        profileDTO.setAvatarUrl( s.getAvatarUrl() );
        profileDTO.setBio( s.getBio() );
        profileDTO.setCity( s.getCity() );
        profileDTO.setStatus( s.getStatus() );
        profileDTO.setIsVerified( s.getIsVerified() );
        profileDTO.setCreatedAt( s.getCreatedAt() );
        profileDTO.setUpdatedAt( s.getUpdatedAt() );

        return profileDTO;
    }

    @Override
    public UserDTO toDtoUserLogin(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );
        userDTO.setLogin( user.getLogin() );

        return userDTO;
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
}
