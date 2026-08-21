package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.Post;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.PostDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T09:18:28+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public Post toEntity(PostDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Post post = new Post();

        post.setId( dto.getId() );
        post.setBody( dto.getBody() );
        post.setVisibility( dto.getVisibility() );
        post.setModerationStatus( dto.getModerationStatus() );
        post.setCreatedAt( dto.getCreatedAt() );
        post.setUpdatedAt( dto.getUpdatedAt() );
        post.community( communityDTOToCommunity( dto.getCommunity() ) );
        post.event( eventDTOToEvent( dto.getEvent() ) );
        post.author( profileDTOToProfile( dto.getAuthor() ) );

        return post;
    }

    @Override
    public List<Post> toEntity(List<PostDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Post> list = new ArrayList<Post>( dtoList.size() );
        for ( PostDTO postDTO : dtoList ) {
            list.add( toEntity( postDTO ) );
        }

        return list;
    }

    @Override
    public List<PostDTO> toDto(List<Post> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PostDTO> list = new ArrayList<PostDTO>( entityList.size() );
        for ( Post post : entityList ) {
            list.add( toDto( post ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Post entity, PostDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getBody() != null ) {
            entity.setBody( dto.getBody() );
        }
        if ( dto.getVisibility() != null ) {
            entity.setVisibility( dto.getVisibility() );
        }
        if ( dto.getModerationStatus() != null ) {
            entity.setModerationStatus( dto.getModerationStatus() );
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
        if ( dto.getEvent() != null ) {
            if ( entity.getEvent() == null ) {
                entity.event( new Event() );
            }
            eventDTOToEvent1( dto.getEvent(), entity.getEvent() );
        }
        if ( dto.getAuthor() != null ) {
            if ( entity.getAuthor() == null ) {
                entity.author( new Profile() );
            }
            profileDTOToProfile1( dto.getAuthor(), entity.getAuthor() );
        }
    }

    @Override
    public PostDTO toDto(Post s) {
        if ( s == null ) {
            return null;
        }

        PostDTO postDTO = new PostDTO();

        postDTO.setCommunity( toDtoCommunityId( s.getCommunity() ) );
        postDTO.setEvent( toDtoEventId( s.getEvent() ) );
        postDTO.setAuthor( toDtoProfileId( s.getAuthor() ) );
        postDTO.setId( s.getId() );
        postDTO.setBody( s.getBody() );
        postDTO.setVisibility( s.getVisibility() );
        postDTO.setModerationStatus( s.getModerationStatus() );
        postDTO.setCreatedAt( s.getCreatedAt() );
        postDTO.setUpdatedAt( s.getUpdatedAt() );

        return postDTO;
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
    public EventDTO toDtoEventId(Event event) {
        if ( event == null ) {
            return null;
        }

        EventDTO eventDTO = new EventDTO();

        eventDTO.setId( event.getId() );

        return eventDTO;
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

    protected Event eventDTOToEvent(EventDTO eventDTO) {
        if ( eventDTO == null ) {
            return null;
        }

        Event event = new Event();

        event.setId( eventDTO.getId() );
        event.setTitle( eventDTO.getTitle() );
        event.setDescription( eventDTO.getDescription() );
        event.setStartsAt( eventDTO.getStartsAt() );
        event.setEndsAt( eventDTO.getEndsAt() );
        event.setLocationName( eventDTO.getLocationName() );
        event.setAddressText( eventDTO.getAddressText() );
        event.setLatitude( eventDTO.getLatitude() );
        event.setLongitude( eventDTO.getLongitude() );
        event.setCoverImageUrl( eventDTO.getCoverImageUrl() );
        event.setCapacity( eventDTO.getCapacity() );
        event.setStatus( eventDTO.getStatus() );
        event.setRejectionReason( eventDTO.getRejectionReason() );
        event.setPublishedAt( eventDTO.getPublishedAt() );
        event.setPublishedToDiscovery( eventDTO.getPublishedToDiscovery() );
        event.setGroupModerationStatus( eventDTO.getGroupModerationStatus() );
        event.setPlatformModerationStatus( eventDTO.getPlatformModerationStatus() );
        event.setCancelledAt( eventDTO.getCancelledAt() );
        event.setCancellationReason( eventDTO.getCancellationReason() );
        event.setCreatedAt( eventDTO.getCreatedAt() );
        event.setUpdatedAt( eventDTO.getUpdatedAt() );
        event.community( communityDTOToCommunity( eventDTO.getCommunity() ) );
        event.category( communityDTOToCommunity( eventDTO.getCategory() ) );
        event.createdBy( profileDTOToProfile( eventDTO.getCreatedBy() ) );
        event.cancelledBy( profileDTOToProfile( eventDTO.getCancelledBy() ) );

        return event;
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

    protected void eventDTOToEvent1(EventDTO eventDTO, Event mappingTarget) {
        if ( eventDTO == null ) {
            return;
        }

        if ( eventDTO.getId() != null ) {
            mappingTarget.setId( eventDTO.getId() );
        }
        if ( eventDTO.getTitle() != null ) {
            mappingTarget.setTitle( eventDTO.getTitle() );
        }
        if ( eventDTO.getDescription() != null ) {
            mappingTarget.setDescription( eventDTO.getDescription() );
        }
        if ( eventDTO.getStartsAt() != null ) {
            mappingTarget.setStartsAt( eventDTO.getStartsAt() );
        }
        if ( eventDTO.getEndsAt() != null ) {
            mappingTarget.setEndsAt( eventDTO.getEndsAt() );
        }
        if ( eventDTO.getLocationName() != null ) {
            mappingTarget.setLocationName( eventDTO.getLocationName() );
        }
        if ( eventDTO.getAddressText() != null ) {
            mappingTarget.setAddressText( eventDTO.getAddressText() );
        }
        if ( eventDTO.getLatitude() != null ) {
            mappingTarget.setLatitude( eventDTO.getLatitude() );
        }
        if ( eventDTO.getLongitude() != null ) {
            mappingTarget.setLongitude( eventDTO.getLongitude() );
        }
        if ( eventDTO.getCoverImageUrl() != null ) {
            mappingTarget.setCoverImageUrl( eventDTO.getCoverImageUrl() );
        }
        if ( eventDTO.getCapacity() != null ) {
            mappingTarget.setCapacity( eventDTO.getCapacity() );
        }
        if ( eventDTO.getStatus() != null ) {
            mappingTarget.setStatus( eventDTO.getStatus() );
        }
        if ( eventDTO.getRejectionReason() != null ) {
            mappingTarget.setRejectionReason( eventDTO.getRejectionReason() );
        }
        if ( eventDTO.getPublishedAt() != null ) {
            mappingTarget.setPublishedAt( eventDTO.getPublishedAt() );
        }
        if ( eventDTO.getPublishedToDiscovery() != null ) {
            mappingTarget.setPublishedToDiscovery( eventDTO.getPublishedToDiscovery() );
        }
        if ( eventDTO.getGroupModerationStatus() != null ) {
            mappingTarget.setGroupModerationStatus( eventDTO.getGroupModerationStatus() );
        }
        if ( eventDTO.getPlatformModerationStatus() != null ) {
            mappingTarget.setPlatformModerationStatus( eventDTO.getPlatformModerationStatus() );
        }
        if ( eventDTO.getCancelledAt() != null ) {
            mappingTarget.setCancelledAt( eventDTO.getCancelledAt() );
        }
        if ( eventDTO.getCancellationReason() != null ) {
            mappingTarget.setCancellationReason( eventDTO.getCancellationReason() );
        }
        if ( eventDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( eventDTO.getCreatedAt() );
        }
        if ( eventDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( eventDTO.getUpdatedAt() );
        }
        if ( eventDTO.getCommunity() != null ) {
            if ( mappingTarget.getCommunity() == null ) {
                mappingTarget.community( new Community() );
            }
            communityDTOToCommunity1( eventDTO.getCommunity(), mappingTarget.getCommunity() );
        }
        if ( eventDTO.getCategory() != null ) {
            if ( mappingTarget.getCategory() == null ) {
                mappingTarget.category( new Community() );
            }
            communityDTOToCommunity1( eventDTO.getCategory(), mappingTarget.getCategory() );
        }
        if ( eventDTO.getCreatedBy() != null ) {
            if ( mappingTarget.getCreatedBy() == null ) {
                mappingTarget.createdBy( new Profile() );
            }
            profileDTOToProfile1( eventDTO.getCreatedBy(), mappingTarget.getCreatedBy() );
        }
        if ( eventDTO.getCancelledBy() != null ) {
            if ( mappingTarget.getCancelledBy() == null ) {
                mappingTarget.cancelledBy( new Profile() );
            }
            profileDTOToProfile1( eventDTO.getCancelledBy(), mappingTarget.getCancelledBy() );
        }
    }
}
