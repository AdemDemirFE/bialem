package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.StoryView;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.StoryDTO;
import com.bialem.backend.service.dto.StoryViewDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-19T16:45:32+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class StoryViewMapperImpl implements StoryViewMapper {

    @Override
    public StoryView toEntity(StoryViewDTO dto) {
        if ( dto == null ) {
            return null;
        }

        StoryView storyView = new StoryView();

        storyView.setId( dto.getId() );
        storyView.setViewedAt( dto.getViewedAt() );
        storyView.story( storyDTOToStory( dto.getStory() ) );
        storyView.viewer( profileDTOToProfile( dto.getViewer() ) );

        return storyView;
    }

    @Override
    public List<StoryView> toEntity(List<StoryViewDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<StoryView> list = new ArrayList<StoryView>( dtoList.size() );
        for ( StoryViewDTO storyViewDTO : dtoList ) {
            list.add( toEntity( storyViewDTO ) );
        }

        return list;
    }

    @Override
    public List<StoryViewDTO> toDto(List<StoryView> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<StoryViewDTO> list = new ArrayList<StoryViewDTO>( entityList.size() );
        for ( StoryView storyView : entityList ) {
            list.add( toDto( storyView ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(StoryView arg0, StoryViewDTO arg1) {
        if ( arg1 == null ) {
            return;
        }

        if ( arg1.getId() != null ) {
            arg0.setId( arg1.getId() );
        }
        if ( arg1.getViewedAt() != null ) {
            arg0.setViewedAt( arg1.getViewedAt() );
        }
        if ( arg1.getStory() != null ) {
            if ( arg0.getStory() == null ) {
                arg0.story( new Story() );
            }
            storyDTOToStory1( arg1.getStory(), arg0.getStory() );
        }
        if ( arg1.getViewer() != null ) {
            if ( arg0.getViewer() == null ) {
                arg0.viewer( new Profile() );
            }
            profileDTOToProfile1( arg1.getViewer(), arg0.getViewer() );
        }
    }

    @Override
    public StoryViewDTO toDto(StoryView s) {
        if ( s == null ) {
            return null;
        }

        StoryViewDTO storyViewDTO = new StoryViewDTO();

        storyViewDTO.setStory( toDtoStoryId( s.getStory() ) );
        storyViewDTO.setViewer( toDtoProfileId( s.getViewer() ) );
        storyViewDTO.setId( s.getId() );
        storyViewDTO.setViewedAt( s.getViewedAt() );

        return storyViewDTO;
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
}
