package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Comment;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.CommentDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Comment} and its DTO {@link CommentDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper extends EntityMapper<CommentDTO, Comment> {
    @Mapping(target = "author", source = "author", qualifiedByName = "profileId")
    CommentDTO toDto(Comment s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
