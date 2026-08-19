package com.bialem.backend.repository;

import com.bialem.backend.domain.HonorBadge;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HonorBadge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HonorBadgeRepository extends JpaRepository<HonorBadge, Long> {}
