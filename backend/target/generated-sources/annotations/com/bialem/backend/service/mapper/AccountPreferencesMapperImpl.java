package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.AccountPreferences;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.AccountPreferencesDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T15:29:04+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class AccountPreferencesMapperImpl implements AccountPreferencesMapper {

    @Override
    public AccountPreferences toEntity(AccountPreferencesDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AccountPreferences accountPreferences = new AccountPreferences();

        accountPreferences.setId( dto.getId() );
        accountPreferences.setDiscoverable( dto.getDiscoverable() );
        accountPreferences.setShowCity( dto.getShowCity() );
        accountPreferences.setShowFollowConnections( dto.getShowFollowConnections() );
        accountPreferences.setAllowFollows( dto.getAllowFollows() );
        accountPreferences.setRequireFollowApproval( dto.getRequireFollowApproval() );
        accountPreferences.setAllowMessagesFrom( dto.getAllowMessagesFrom() );
        accountPreferences.setNotifyEvents( dto.getNotifyEvents() );
        accountPreferences.setNotifyCommunities( dto.getNotifyCommunities() );
        accountPreferences.setNotifySocial( dto.getNotifySocial() );
        accountPreferences.setNotifyAdvantages( dto.getNotifyAdvantages() );
        accountPreferences.setNotifySystem( dto.getNotifySystem() );
        accountPreferences.setUpdatedAt( dto.getUpdatedAt() );
        accountPreferences.profile( profileDTOToProfile( dto.getProfile() ) );

        return accountPreferences;
    }

    @Override
    public List<AccountPreferences> toEntity(List<AccountPreferencesDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<AccountPreferences> list = new ArrayList<AccountPreferences>( dtoList.size() );
        for ( AccountPreferencesDTO accountPreferencesDTO : dtoList ) {
            list.add( toEntity( accountPreferencesDTO ) );
        }

        return list;
    }

    @Override
    public List<AccountPreferencesDTO> toDto(List<AccountPreferences> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<AccountPreferencesDTO> list = new ArrayList<AccountPreferencesDTO>( entityList.size() );
        for ( AccountPreferences accountPreferences : entityList ) {
            list.add( toDto( accountPreferences ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(AccountPreferences entity, AccountPreferencesDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getDiscoverable() != null ) {
            entity.setDiscoverable( dto.getDiscoverable() );
        }
        if ( dto.getShowCity() != null ) {
            entity.setShowCity( dto.getShowCity() );
        }
        if ( dto.getShowFollowConnections() != null ) {
            entity.setShowFollowConnections( dto.getShowFollowConnections() );
        }
        if ( dto.getAllowFollows() != null ) {
            entity.setAllowFollows( dto.getAllowFollows() );
        }
        if ( dto.getRequireFollowApproval() != null ) {
            entity.setRequireFollowApproval( dto.getRequireFollowApproval() );
        }
        if ( dto.getAllowMessagesFrom() != null ) {
            entity.setAllowMessagesFrom( dto.getAllowMessagesFrom() );
        }
        if ( dto.getNotifyEvents() != null ) {
            entity.setNotifyEvents( dto.getNotifyEvents() );
        }
        if ( dto.getNotifyCommunities() != null ) {
            entity.setNotifyCommunities( dto.getNotifyCommunities() );
        }
        if ( dto.getNotifySocial() != null ) {
            entity.setNotifySocial( dto.getNotifySocial() );
        }
        if ( dto.getNotifyAdvantages() != null ) {
            entity.setNotifyAdvantages( dto.getNotifyAdvantages() );
        }
        if ( dto.getNotifySystem() != null ) {
            entity.setNotifySystem( dto.getNotifySystem() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getProfile() != null ) {
            if ( entity.getProfile() == null ) {
                entity.profile( new Profile() );
            }
            profileDTOToProfile1( dto.getProfile(), entity.getProfile() );
        }
    }

    @Override
    public AccountPreferencesDTO toDto(AccountPreferences s) {
        if ( s == null ) {
            return null;
        }

        AccountPreferencesDTO accountPreferencesDTO = new AccountPreferencesDTO();

        accountPreferencesDTO.setProfile( toDtoProfileId( s.getProfile() ) );
        accountPreferencesDTO.setId( s.getId() );
        accountPreferencesDTO.setDiscoverable( s.getDiscoverable() );
        accountPreferencesDTO.setShowCity( s.getShowCity() );
        accountPreferencesDTO.setShowFollowConnections( s.getShowFollowConnections() );
        accountPreferencesDTO.setAllowFollows( s.getAllowFollows() );
        accountPreferencesDTO.setRequireFollowApproval( s.getRequireFollowApproval() );
        accountPreferencesDTO.setAllowMessagesFrom( s.getAllowMessagesFrom() );
        accountPreferencesDTO.setNotifyEvents( s.getNotifyEvents() );
        accountPreferencesDTO.setNotifyCommunities( s.getNotifyCommunities() );
        accountPreferencesDTO.setNotifySocial( s.getNotifySocial() );
        accountPreferencesDTO.setNotifyAdvantages( s.getNotifyAdvantages() );
        accountPreferencesDTO.setNotifySystem( s.getNotifySystem() );
        accountPreferencesDTO.setUpdatedAt( s.getUpdatedAt() );

        return accountPreferencesDTO;
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
