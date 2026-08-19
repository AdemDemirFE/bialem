package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Report;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.ReportDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Report} and its DTO {@link ReportDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReportMapper extends EntityMapper<ReportDTO, Report> {
    @Mapping(target = "reporter", source = "reporter", qualifiedByName = "profileId")
    @Mapping(target = "resolvedBy", source = "resolvedBy", qualifiedByName = "profileId")
    ReportDTO toDto(Report s);

    @Named("profileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProfileDTO toDtoProfileId(Profile profile);
}
