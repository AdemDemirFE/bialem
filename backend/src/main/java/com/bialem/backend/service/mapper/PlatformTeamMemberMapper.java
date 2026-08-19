package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PlatformTeamMember;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.PlatformTeamMemberDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlatformTeamMember} and its DTO {@link PlatformTeamMemberDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlatformTeamMemberMapper extends EntityMapper<PlatformTeamMemberDTO, PlatformTeamMember> {
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    @Mapping(target = "assignedBy", source = "assignedBy", qualifiedByName = "profileId")
    PlatformTeamMemberDTO toDto(PlatformTeamMember s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
