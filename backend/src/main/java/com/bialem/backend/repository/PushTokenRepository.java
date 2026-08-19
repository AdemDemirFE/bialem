package com.bialem.backend.repository;

import com.bialem.backend.domain.PushToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PushToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {}
