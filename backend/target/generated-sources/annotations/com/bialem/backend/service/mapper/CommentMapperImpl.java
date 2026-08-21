package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Comment;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.CommentDTO;
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
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toEntity(CommentDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setId( dto.getId() );
        comment.setTargetType( dto.getTargetType() );
        comment.setTargetId( dto.getTargetId() );
        comment.setBody( dto.getBody() );
        comment.setModerationStatus( dto.getModerationStatus() );
        comment.setCreatedAt( dto.getCreatedAt() );
        comment.setUpdatedAt( dto.getUpdatedAt() );
        comment.author( profileDTOToProfile( dto.getAuthor() ) );

        return comment;
    }

    @Override
    public List<Comment> toEntity(List<CommentDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Comment> list = new ArrayList<Comment>( dtoList.size() );
        for ( CommentDTO commentDTO : dtoList ) {
            list.add( toEntity( commentDTO ) );
        }

        return list;
    }

    @Override
    public List<CommentDTO> toDto(List<Comment> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CommentDTO> list = new ArrayList<CommentDTO>( entityList.size() );
        for ( Comment comment : entityList ) {
            list.add( toDto( comment ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Comment entity, CommentDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getTargetType() != null ) {
            entity.setTargetType( dto.getTargetType() );
        }
        if ( dto.getTargetId() != null ) {
            entity.setTargetId( dto.getTargetId() );
        }
        if ( dto.getBody() != null ) {
            entity.setBody( dto.getBody() );
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
        if ( dto.getAuthor() != null ) {
            if ( entity.getAuthor() == null ) {
                entity.author( new Profile() );
            }
            profileDTOToProfile1( dto.getAuthor(), entity.getAuthor() );
        }
    }

    @Override
    public CommentDTO toDto(Comment s) {
        if ( s == null ) {
            return null;
        }

        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setAuthor( toDtoProfileId( s.getAuthor() ) );
        commentDTO.setId( s.getId() );
        commentDTO.setTargetType( s.getTargetType() );
        commentDTO.setTargetId( s.getTargetId() );
        commentDTO.setBody( s.getBody() );
        commentDTO.setModerationStatus( s.getModerationStatus() );
        commentDTO.setCreatedAt( s.getCreatedAt() );
        commentDTO.setUpdatedAt( s.getUpdatedAt() );

        return commentDTO;
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
