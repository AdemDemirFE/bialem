package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Post;
import com.bialem.backend.domain.PostMedia;
import com.bialem.backend.service.dto.PostDTO;
import com.bialem.backend.service.dto.PostMediaDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PostMedia} and its DTO {@link PostMediaDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostMediaMapper extends EntityMapper<PostMediaDTO, PostMedia> {
    @Mapping(target = "post", source = "post", qualifiedByName = "postId")
    PostMediaDTO toDto(PostMedia s);

    @Named("postId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PostDTO toDtoPostId(Post post);
}
