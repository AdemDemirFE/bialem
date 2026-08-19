package com.bialem.backend.repository;

import com.bialem.backend.domain.CityEventTicketOffer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CityEventTicketOffer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityEventTicketOfferRepository extends JpaRepository<CityEventTicketOffer, Long> {}
