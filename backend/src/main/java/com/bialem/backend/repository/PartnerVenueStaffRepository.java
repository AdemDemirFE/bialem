package com.bialem.backend.repository;

import com.bialem.backend.domain.PartnerVenueStaff;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PartnerVenueStaff entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PartnerVenueStaffRepository extends JpaRepository<PartnerVenueStaff, Long> {}
