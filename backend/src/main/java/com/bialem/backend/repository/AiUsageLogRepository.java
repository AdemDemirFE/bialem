package com.bialem.backend.repository;

import com.bialem.backend.domain.AiUsageLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AiUsageLog entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {}
