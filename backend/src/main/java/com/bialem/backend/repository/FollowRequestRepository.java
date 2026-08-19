package com.bialem.backend.repository;

import com.bialem.backend.domain.FollowRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FollowRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {}
