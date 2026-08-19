package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Role;
import com.bialem.backend.domain.UserRole;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.RoleDTO;
import com.bialem.backend.service.dto.UserRoleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserRole} and its DTO {@link UserRoleDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserRoleMapper extends EntityMapper<UserRoleDTO, UserRole> {
    @Mapping(target = "user", source = "user", qualifiedByName = "profileId")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleId")
    UserRoleDTO toDto(UserRole s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);

    @Named("roleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoleDTO toDtoRoleId(Role role);
}
