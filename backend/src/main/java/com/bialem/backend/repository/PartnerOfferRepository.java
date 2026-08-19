package com.bialem.backend.repository;

import com.bialem.backend.domain.PartnerOffer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartnerOffer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartnerOfferRepository extends JpaRepository<PartnerOffer, Long> {}
