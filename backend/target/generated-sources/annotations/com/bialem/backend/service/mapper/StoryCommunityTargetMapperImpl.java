package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.StoryCommunityTarget;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.StoryCommunityTargetDTO;
import com.bialem.backend.service.dto.StoryDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T00:11:47+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class StoryCommunityTargetMapperImpl implements StoryCommunityTargetMapper {

    @Override
    public StoryCommunityTarget toEntity(StoryCommunityTargetDTO dto) {
        if ( dto == null ) {
            return null;
        }

        StoryCommunityTarget storyCommunityTarget = new StoryCommunityTarget();

        storyCommunityTarget.setId( dto.getId() );
        storyCommunityTarget.setCreatedAt( dto.getCreatedAt() );
        storyCommunityTarget.story( storyDTOToStory( dto.getStory() ) );
        storyCommunityTarget.community( communityDTOToCommunity( dto.getCommunity() ) );

        return storyCommunityTarget;
    }

    @Override
    public List<StoryCommunityTarget> toEntity(List<StoryCommunityTargetDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<StoryCommunityTarget> list = new ArrayList<StoryCommunityTarget>( dtoList.size() );
        for ( StoryCommunityTargetDTO storyCommunityTargetDTO : dtoList ) {
            list.add( toEntity( storyCommunityTargetDTO ) );
        }

        return list;
    }

    @Override
    public List<StoryCommunityTargetDTO> toDto(List<StoryCommunityTarget> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<StoryCommunityTargetDTO> list = new ArrayList<StoryCommunityTargetDTO>( entityList.size() );
        for ( StoryCommunityTarget storyCommunityTarget : entityList ) {
            list.add( toDto( storyCommunityTarget ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(StoryCommunityTarget entity, StoryCommunityTargetDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getStory() != null ) {
            if ( entity.getStory() == null ) {
                entity.story( new Story() );
            }
            storyDTOToStory1( dto.getStory(), entity.getStory() );
        }
        if ( dto.getCommunity() != null ) {
            if ( entity.getCommunity() == null ) {
                entity.community( new Community() );
            }
            communityDTOToCommunity1( dto.getCommunity(), entity.getCommunity() );
        }
    }

    @Override
    public StoryCommunityTargetDTO toDto(StoryCommunityTarget s) {
        if ( s == null ) {
            return null;
        }

        StoryCommunityTargetDTO storyCommunityTargetDTO = new StoryCommunityTargetDTO();

        storyCommunityTargetDTO.setStory( toDtoStoryId( s.getStory() ) );
        storyCommunityTargetDTO.setCommunity( toDtoCommunityId( s.getCommunity() ) );
        storyCommunityTargetDTO.setId( s.getId() );
        storyCommunityTargetDTO.setCreatedAt( s.getCreatedAt() );

        return storyCommunityTargetDTO;
    }

    @Override
    public StoryDTO toDtoStoryId(Story story) {
        if ( story == null ) {
            return null;
        }

        StoryDTO storyDTO = new StoryDTO();

        storyDTO.setId( story.getId() );

        return storyDTO;
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

    protected Story storyDTOToStory(StoryDTO storyDTO) {
        if ( storyDTO == null ) {
            return null;
        }

        Story story = new Story();

        story.setId( storyDTO.getId() );
        story.setContentType( storyDTO.getContentType() );
        story.setBody( storyDTO.getBody() );
        story.setMediaUrl( storyDTO.getMediaUrl() );
        story.setIsPublic( storyDTO.getIsPublic() );
        story.setShareWithFollowers( storyDTO.getShareWithFollowers() );
        story.setCreatedAt( storyDTO.getCreatedAt() );
        story.setExpiresAt( storyDTO.getExpiresAt() );
        story.author( profileDTOToProfile( storyDTO.getAuthor() ) );

        return story;
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

    protected void storyDTOToStory1(StoryDTO storyDTO, Story mappingTarget) {
        if ( storyDTO == null ) {
            return;
        }

        if ( storyDTO.getId() != null ) {
            mappingTarget.setId( storyDTO.getId() );
        }
        if ( storyDTO.getContentType() != null ) {
            mappingTarget.setContentType( storyDTO.getContentType() );
        }
        if ( storyDTO.getBody() != null ) {
            mappingTarget.setBody( storyDTO.getBody() );
        }
        if ( storyDTO.getMediaUrl() != null ) {
            mappingTarget.setMediaUrl( storyDTO.getMediaUrl() );
        }
        if ( storyDTO.getIsPublic() != null ) {
            mappingTarget.setIsPublic( storyDTO.getIsPublic() );
        }
        if ( storyDTO.getShareWithFollowers() != null ) {
            mappingTarget.setShareWithFollowers( storyDTO.getShareWithFollowers() );
        }
        if ( storyDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( storyDTO.getCreatedAt() );
        }
        if ( storyDTO.getExpiresAt() != null ) {
            mappingTarget.setExpiresAt( storyDTO.getExpiresAt() );
        }
        if ( storyDTO.getAuthor() != null ) {
            if ( mappingTarget.getAuthor() == null ) {
                mappingTarget.author( new Profile() );
            }
            profileDTOToProfile1( storyDTO.getAuthor(), mappingTarget.getAuthor() );
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
