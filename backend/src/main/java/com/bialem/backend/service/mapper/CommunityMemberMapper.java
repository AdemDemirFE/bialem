package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Community;
import com.bialem.backend.domain.CommunityMember;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.CommunityDTO;
import com.bialem.backend.service.dto.CommunityMemberDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for the entity {@link CommunityMember} and its DTO {@link CommunityMemberDTO}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommunityMemberMapper extends EntityMapper<CommunityMemberDTO, CommunityMember> {
    @Mapping(target = "community", source = "community", qualifiedByName = "communityId")
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    CommunityMemberDTO toDto(CommunityMember s);

    @Named("communityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CommunityDTO toDtoCommunityId(Community community);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
