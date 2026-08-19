package com.bialem.backend.repository;

import com.bialem.backend.domain.AiUsageLog;
import java.time.Instant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AiUsageLog entity.
 */
@Repository
public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
    long countByUser_IdAndCreatedAtAfter(Long userId, Instant createdAt);
}
