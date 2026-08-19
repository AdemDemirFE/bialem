package com.bialem.backend.repository;

import com.bialem.backend.domain.CityEventSyncLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CityEventSyncLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityEventSyncLogRepository extends JpaRepository<CityEventSyncLog, Long> {}
