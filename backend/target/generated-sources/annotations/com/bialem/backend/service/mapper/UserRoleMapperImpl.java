package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Role;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.UserRole;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.RoleDTO;
import com.bialem.backend.service.dto.UserDTO;
import com.bialem.backend.service.dto.UserRoleDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T15:29:06+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class UserRoleMapperImpl implements UserRoleMapper {

    @Override
    public UserRole toEntity(UserRoleDTO dto) {
        if ( dto == null ) {
            return null;
        }

        UserRole userRole = new UserRole();

        userRole.setId( dto.getId() );
        userRole.setCreatedAt( dto.getCreatedAt() );
        userRole.user( profileDTOToProfile( dto.getUser() ) );
        userRole.role( roleDTOToRole( dto.getRole() ) );

        return userRole;
    }

    @Override
    public List<UserRole> toEntity(List<UserRoleDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<UserRole> list = new ArrayList<UserRole>( dtoList.size() );
        for ( UserRoleDTO userRoleDTO : dtoList ) {
            list.add( toEntity( userRoleDTO ) );
        }

        return list;
    }

    @Override
    public List<UserRoleDTO> toDto(List<UserRole> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<UserRoleDTO> list = new ArrayList<UserRoleDTO>( entityList.size() );
        for ( UserRole userRole : entityList ) {
            list.add( toDto( userRole ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(UserRole entity, UserRoleDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
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
        if ( dto.getRole() != null ) {
            if ( entity.getRole() == null ) {
                entity.role( new Role() );
            }
            roleDTOToRole1( dto.getRole(), entity.getRole() );
        }
    }

    @Override
    public UserRoleDTO toDto(UserRole s) {
        if ( s == null ) {
            return null;
        }

        UserRoleDTO userRoleDTO = new UserRoleDTO();

        userRoleDTO.setUser( toDtoProfileId( s.getUser() ) );
        userRoleDTO.setRole( toDtoRoleId( s.getRole() ) );
        userRoleDTO.setId( s.getId() );
        userRoleDTO.setCreatedAt( s.getCreatedAt() );

        return userRoleDTO;
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
    public RoleDTO toDtoRoleId(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDTO roleDTO = new RoleDTO();

        roleDTO.setId( role.getId() );

        return roleDTO;
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

    protected Role roleDTOToRole(RoleDTO roleDTO) {
        if ( roleDTO == null ) {
            return null;
        }

        Role role = new Role();

        role.setId( roleDTO.getId() );
        role.setCode( roleDTO.getCode() );
        role.setName( roleDTO.getName() );
        role.setCreatedAt( roleDTO.getCreatedAt() );

        return role;
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

    protected void roleDTOToRole1(RoleDTO roleDTO, Role mappingTarget) {
        if ( roleDTO == null ) {
            return;
        }

        if ( roleDTO.getId() != null ) {
            mappingTarget.setId( roleDTO.getId() );
        }
        if ( roleDTO.getCode() != null ) {
            mappingTarget.setCode( roleDTO.getCode() );
        }
        if ( roleDTO.getName() != null ) {
            mappingTarget.setName( roleDTO.getName() );
        }
        if ( roleDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( roleDTO.getCreatedAt() );
        }
    }
}
