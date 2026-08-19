package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Story;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.StoryDTO;
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
public class StoryMapperImpl implements StoryMapper {

    @Override
    public Story toEntity(StoryDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Story story = new Story();

        story.setId( dto.getId() );
        story.setContentType( dto.getContentType() );
        story.setBody( dto.getBody() );
        story.setMediaUrl( dto.getMediaUrl() );
        story.setIsPublic( dto.getIsPublic() );
        story.setShareWithFollowers( dto.getShareWithFollowers() );
        story.setCreatedAt( dto.getCreatedAt() );
        story.setExpiresAt( dto.getExpiresAt() );
        story.author( profileDTOToProfile( dto.getAuthor() ) );

        return story;
    }

    @Override
    public List<Story> toEntity(List<StoryDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Story> list = new ArrayList<Story>( dtoList.size() );
        for ( StoryDTO storyDTO : dtoList ) {
            list.add( toEntity( storyDTO ) );
        }

        return list;
    }

    @Override
    public List<StoryDTO> toDto(List<Story> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<StoryDTO> list = new ArrayList<StoryDTO>( entityList.size() );
        for ( Story story : entityList ) {
            list.add( toDto( story ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Story arg0, StoryDTO arg1) {
        if ( arg1 == null ) {
            return;
        }

        if ( arg1.getId() != null ) {
            arg0.setId( arg1.getId() );
        }
        if ( arg1.getContentType() != null ) {
            arg0.setContentType( arg1.getContentType() );
        }
        if ( arg1.getBody() != null ) {
            arg0.setBody( arg1.getBody() );
        }
        if ( arg1.getMediaUrl() != null ) {
            arg0.setMediaUrl( arg1.getMediaUrl() );
        }
        if ( arg1.getIsPublic() != null ) {
            arg0.setIsPublic( arg1.getIsPublic() );
        }
        if ( arg1.getShareWithFollowers() != null ) {
            arg0.setShareWithFollowers( arg1.getShareWithFollowers() );
        }
        if ( arg1.getCreatedAt() != null ) {
            arg0.setCreatedAt( arg1.getCreatedAt() );
        }
        if ( arg1.getExpiresAt() != null ) {
            arg0.setExpiresAt( arg1.getExpiresAt() );
        }
        if ( arg1.getAuthor() != null ) {
            if ( arg0.getAuthor() == null ) {
                arg0.author( new Profile() );
            }
            profileDTOToProfile1( arg1.getAuthor(), arg0.getAuthor() );
        }
    }

    @Override
    public StoryDTO toDto(Story s) {
        if ( s == null ) {
            return null;
        }

        StoryDTO storyDTO = new StoryDTO();

        storyDTO.setAuthor( toDtoProfileId( s.getAuthor() ) );
        storyDTO.setId( s.getId() );
        storyDTO.setContentType( s.getContentType() );
        storyDTO.setBody( s.getBody() );
        storyDTO.setMediaUrl( s.getMediaUrl() );
        storyDTO.setIsPublic( s.getIsPublic() );
        storyDTO.setShareWithFollowers( s.getShareWithFollowers() );
        storyDTO.setCreatedAt( s.getCreatedAt() );
        storyDTO.setExpiresAt( s.getExpiresAt() );

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
