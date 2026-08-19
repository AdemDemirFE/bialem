package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T16:30:52+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEntity(EventDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Event event = new Event();

        event.setId( dto.getId() );
        event.setTitle( dto.getTitle() );
        event.setDescription( dto.getDescription() );
        event.setStartsAt( dto.getStartsAt() );
        event.setEndsAt( dto.getEndsAt() );
        event.setLocationName( dto.getLocationName() );
        event.setAddressText( dto.getAddressText() );
        event.setLatitude( dto.getLatitude() );
        event.setLongitude( dto.getLongitude() );
        event.setCoverImageUrl( dto.getCoverImageUrl() );
        event.setCapacity( dto.getCapacity() );
        event.setStatus( dto.getStatus() );
        event.setRejectionReason( dto.getRejectionReason() );
        event.setPublishedAt( dto.getPublishedAt() );
        event.setPublishedToDiscovery( dto.getPublishedToDiscovery() );
        event.setGroupModerationStatus( dto.getGroupModerationStatus() );
        event.setPlatformModerationStatus( dto.getPlatformModerationStatus() );
        event.setCancelledAt( dto.getCancelledAt() );
        event.setCancellationReason( dto.getCancellationReason() );
        event.setCreatedAt( dto.getCreatedAt() );
        event.setUpdatedAt( dto.getUpdatedAt() );
        event.community( communityDTOToCommunity( dto.getCommunity() ) );
        event.category( communityDTOToCommunity( dto.getCategory() ) );
        event.createdBy( profileDTOToProfile( dto.getCreatedBy() ) );
        event.cancelledBy( profileDTOToProfile( dto.getCancelledBy() ) );

        return event;
    }

    @Override
    public List<Event> toEntity(List<EventDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Event> list = new ArrayList<Event>( dtoList.size() );
        for ( EventDTO eventDTO : dtoList ) {
            list.add( toEntity( eventDTO ) );
        }

        return list;
    }

    @Override
    public List<EventDTO> toDto(List<Event> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<EventDTO> list = new ArrayList<EventDTO>( entityList.size() );
        for ( Event event : entityList ) {
            list.add( toDto( event ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Event entity, EventDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getTitle() != null ) {
            entity.setTitle( dto.getTitle() );
        }
        if ( dto.getDescription() != null ) {
            entity.setDescription( dto.getDescription() );
        }
        if ( dto.getStartsAt() != null ) {
            entity.setStartsAt( dto.getStartsAt() );
        }
        if ( dto.getEndsAt() != null ) {
            entity.setEndsAt( dto.getEndsAt() );
        }
        if ( dto.getLocationName() != null ) {
            entity.setLocationName( dto.getLocationName() );
        }
        if ( dto.getAddressText() != null ) {
            entity.setAddressText( dto.getAddressText() );
        }
        if ( dto.getLatitude() != null ) {
            entity.setLatitude( dto.getLatitude() );
        }
        if ( dto.getLongitude() != null ) {
            entity.setLongitude( dto.getLongitude() );
        }
        if ( dto.getCoverImageUrl() != null ) {
            entity.setCoverImageUrl( dto.getCoverImageUrl() );
        }
        if ( dto.getCapacity() != null ) {
            entity.setCapacity( dto.getCapacity() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getRejectionReason() != null ) {
            entity.setRejectionReason( dto.getRejectionReason() );
        }
        if ( dto.getPublishedAt() != null ) {
            entity.setPublishedAt( dto.getPublishedAt() );
        }
        if ( dto.getPublishedToDiscovery() != null ) {
            entity.setPublishedToDiscovery( dto.getPublishedToDiscovery() );
        }
        if ( dto.getGroupModerationStatus() != null ) {
            entity.setGroupModerationStatus( dto.getGroupModerationStatus() );
        }
        if ( dto.getPlatformModerationStatus() != null ) {
            entity.setPlatformModerationStatus( dto.getPlatformModerationStatus() );
        }
        if ( dto.getCancelledAt() != null ) {
            entity.setCancelledAt( dto.getCancelledAt() );
        }
        if ( dto.getCancellationReason() != null ) {
            entity.setCancellationReason( dto.getCancellationReason() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getCommunity() != null ) {
            if ( entity.getCommunity() == null ) {
                entity.community( new Community() );
            }
            communityDTOToCommunity1( dto.getCommunity(), entity.getCommunity() );
        }
        if ( dto.getCategory() != null ) {
            if ( entity.getCategory() == null ) {
                entity.category( new Community() );
            }
            communityDTOToCommunity1( dto.getCategory(), entity.getCategory() );
        }
        if ( dto.getCreatedBy() != null ) {
            if ( entity.getCreatedBy() == null ) {
                entity.createdBy( new Profile() );
            }
            profileDTOToProfile1( dto.getCreatedBy(), entity.getCreatedBy() );
        }
        if ( dto.getCancelledBy() != null ) {
            if ( entity.getCancelledBy() == null ) {
                entity.cancelledBy( new Profile() );
            }
            profileDTOToProfile1( dto.getCancelledBy(), entity.getCancelledBy() );
        }
    }

    @Override
    public EventDTO toDto(Event s) {
        if ( s == null ) {
            return null;
        }

        EventDTO eventDTO = new EventDTO();

        eventDTO.setCommunity( toDtoCommunityId( s.getCommunity() ) );
        eventDTO.setCategory( toDtoCommunityId( s.getCategory() ) );
        eventDTO.setCreatedBy( toDtoProfileId( s.getCreatedBy() ) );
        eventDTO.setCancelledBy( toDtoProfileId( s.getCancelledBy() ) );
        eventDTO.setId( s.getId() );
        eventDTO.setTitle( s.getTitle() );
        eventDTO.setDescription( s.getDescription() );
        eventDTO.setStartsAt( s.getStartsAt() );
        eventDTO.setEndsAt( s.getEndsAt() );
        eventDTO.setLocationName( s.getLocationName() );
        eventDTO.setAddressText( s.getAddressText() );
        eventDTO.setLatitude( s.getLatitude() );
        eventDTO.setLongitude( s.getLongitude() );
        eventDTO.setCoverImageUrl( s.getCoverImageUrl() );
        eventDTO.setCapacity( s.getCapacity() );
        eventDTO.setStatus( s.getStatus() );
        eventDTO.setRejectionReason( s.getRejectionReason() );
        eventDTO.setPublishedAt( s.getPublishedAt() );
        eventDTO.setPublishedToDiscovery( s.getPublishedToDiscovery() );
        eventDTO.setGroupModerationStatus( s.getGroupModerationStatus() );
        eventDTO.setPlatformModerationStatus( s.getPlatformModerationStatus() );
        eventDTO.setCancelledAt( s.getCancelledAt() );
        eventDTO.setCancellationReason( s.getCancellationReason() );
        eventDTO.setCreatedAt( s.getCreatedAt() );
        eventDTO.setUpdatedAt( s.getUpdatedAt() );

        return eventDTO;
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
