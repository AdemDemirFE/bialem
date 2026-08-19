package com.bialem.backend.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bialem.backend.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityEventTicketOfferDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CityEventTicketOfferDTO.class);
        CityEventTicketOfferDTO cityEventTicketOfferDTO1 = new CityEventTicketOfferDTO();
        cityEventTicketOfferDTO1.setId(1L);
        CityEventTicketOfferDTO cityEventTicketOfferDTO2 = new CityEventTicketOfferDTO();
        assertThat(cityEventTicketOfferDTO1).isNotEqualTo(cityEventTicketOfferDTO2);
        cityEventTicketOfferDTO2.setId(cityEventTicketOfferDTO1.getId());
        assertThat(cityEventTicketOfferDTO1).isEqualTo(cityEventTicketOfferDTO2);
        cityEventTicketOfferDTO2.setId(2L);
        assertThat(cityEventTicketOfferDTO1).isNotEqualTo(cityEventTicketOfferDTO2);
        cityEventTicketOfferDTO1.setId(null);
        assertThat(cityEventTicketOfferDTO1).isNotEqualTo(cityEventTicketOfferDTO2);
    }
}
