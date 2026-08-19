package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.AccountPreferences;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.dto.AccountPreferencesDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AccountPreferences} and its DTO {@link AccountPreferencesDTO}.
 */
@Mapper(componentModel = "spring")
public interface AccountPreferencesMapper extends EntityMapper<AccountPreferencesDTO, AccountPreferences> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "profileId")
    AccountPreferencesDTO toDto(AccountPreferences s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
