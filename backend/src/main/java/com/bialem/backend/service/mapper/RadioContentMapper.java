package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.RadioContent;
import com.bialem.backend.service.dto.RadioContentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RadioContent} and its DTO {@link RadioContentDTO}.
 */
@Mapper(componentModel = "spring")
public interface RadioContentMapper extends EntityMapper<RadioContentDTO, RadioContent> {}
