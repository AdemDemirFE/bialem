package com.bialem.backend.repository;

import com.bialem.backend.domain.PartnerOfferRedemption;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartnerOfferRedemption entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartnerOfferRedemptionRepository extends JpaRepository<PartnerOfferRedemption, Long> {}
