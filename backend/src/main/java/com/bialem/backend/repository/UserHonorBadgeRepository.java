package com.bialem.backend.repository;

import com.bialem.backend.domain.UserHonorBadge;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserHonorBadge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserHonorBadgeRepository extends JpaRepository<UserHonorBadge, Long> {}
