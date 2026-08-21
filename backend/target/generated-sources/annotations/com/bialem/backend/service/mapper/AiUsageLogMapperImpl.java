package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.AiUsageLog;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.AiUsageLogDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T10:10:43+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class AiUsageLogMapperImpl implements AiUsageLogMapper {

    @Override
    public AiUsageLog toEntity(AiUsageLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AiUsageLog aiUsageLog = new AiUsageLog();

        aiUsageLog.setId( dto.getId() );
        aiUsageLog.setCreatedAt( dto.getCreatedAt() );
        aiUsageLog.user( profileDTOToProfile( dto.getUser() ) );

        return aiUsageLog;
    }

    @Override
    public List<AiUsageLog> toEntity(List<AiUsageLogDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<AiUsageLog> list = new ArrayList<AiUsageLog>( dtoList.size() );
        for ( AiUsageLogDTO aiUsageLogDTO : dtoList ) {
            list.add( toEntity( aiUsageLogDTO ) );
        }

        return list;
    }

    @Override
    public List<AiUsageLogDTO> toDto(List<AiUsageLog> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<AiUsageLogDTO> list = new ArrayList<AiUsageLogDTO>( entityList.size() );
        for ( AiUsageLog aiUsageLog : entityList ) {
            list.add( toDto( aiUsageLog ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(AiUsageLog entity, AiUsageLogDTO dto) {
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
    }

    @Override
    public AiUsageLogDTO toDto(AiUsageLog s) {
        if ( s == null ) {
            return null;
        }

        AiUsageLogDTO aiUsageLogDTO = new AiUsageLogDTO();

        aiUsageLogDTO.setUser( toDtoProfileId( s.getUser() ) );
        aiUsageLogDTO.setId( s.getId() );
        aiUsageLogDTO.setCreatedAt( s.getCreatedAt() );

        return aiUsageLogDTO;
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
