package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryCommunityTarget;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoryCommunityTarget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoryCommunityTargetRepository extends JpaRepository<StoryCommunityTarget, Long> {}
