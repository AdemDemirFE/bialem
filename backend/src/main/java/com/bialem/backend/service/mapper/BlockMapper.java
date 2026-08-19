package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Block;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.BlockDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Block} and its DTO {@link BlockDTO}.
 */
@Mapper(componentModel = "spring")
public interface BlockMapper extends EntityMapper<BlockDTO, Block> {
    @Mapping(target = "blocker", source = "blocker", qualifiedByName = "profileId")
    @Mapping(target = "blockedUser", source = "blockedUser", qualifiedByName = "profileId")
    BlockDTO toDto(Block s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
