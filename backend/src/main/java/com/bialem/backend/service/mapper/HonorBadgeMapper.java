package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.HonorBadge;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.HonorBadgeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HonorBadge} and its DTO {@link HonorBadgeDTO}.
 */
@Mapper(componentModel = "spring")
public interface HonorBadgeMapper extends EntityMapper<HonorBadgeDTO, HonorBadge> {
    @Mapping(target = "community", source = "community", qualifiedByName = "communityId")
    HonorBadgeDTO toDto(HonorBadge s);

    @Named("communityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityDTO toDtoCommunityId(Community community);
}
