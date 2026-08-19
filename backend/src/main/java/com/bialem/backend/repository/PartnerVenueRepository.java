package com.bialem.backend.repository;

import com.bialem.backend.domain.PartnerVenue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartnerVenue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartnerVenueRepository extends JpaRepository<PartnerVenue, Long>, JpaSpecificationExecutor<PartnerVenue> {}
