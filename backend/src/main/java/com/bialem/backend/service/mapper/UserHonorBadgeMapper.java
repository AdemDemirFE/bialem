package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.HonorBadge;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.UserHonorBadge;
import com.bialem.backend.service.dto.HonorBadgeDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.UserHonorBadgeDTO;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for the entity {@link UserHonorBadge} and its DTO {@link UserHonorBadgeDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserHonorBadgeMapper extends EntityMapper<UserHonorBadgeDTO, UserHonorBadge> {
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    @Mapping(target = "badge", source = "badge", qualifiedByName = "honorBadgeId")
    @Mapping(target = "awardedBy", source = "awardedBy", qualifiedByName = "profileId")
    UserHonorBadgeDTO toDto(UserHonorBadge s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("honorBadgeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    HonorBadgeDTO toDtoHonorBadgeId(HonorBadge honorBadge);
}
