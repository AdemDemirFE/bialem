package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEvent;
import com.bialem.backend.domain.CityEventTicketOffer;
import com.bialem.backend.service.dto.CityEventDTO;
import com.bialem.backend.service.dto.CityEventTicketOfferDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CityEventTicketOffer} and its DTO {@link CityEventTicketOfferDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityEventTicketOfferMapper extends EntityMapper<CityEventTicketOfferDTO, CityEventTicketOffer> {
    @Mapping(target = "cityEvent", source = "cityEvent", qualifiedByName = "cityEventId")
    CityEventTicketOfferDTO toDto(CityEventTicketOffer s);

    @Named("cityEventId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CityEventDTO toDtoCityEventId(CityEvent cityEvent);
}
