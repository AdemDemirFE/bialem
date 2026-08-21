package com.bialem.backend.repository;

import com.bialem.backend.domain.PushDeviceToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PushDeviceToken entity.
 */
@Repository
public interface PushDeviceTokenRepository extends JpaRepository<PushDeviceToken, Long> {
    Optional<PushDeviceToken> findByToken(String token);

    List<PushDeviceToken> findByUser_Id(Long userId);

    List<PushDeviceToken> findByUser_IdAndActiveTrue(Long userId);

    @Modifying
    @Query("UPDATE PushDeviceToken d SET d.active = false WHERE d.lastSeenAt < :cutoff AND d.active = true")
    int deactivateStaleDevices(@Param("cutoff") Instant cutoff);

    @Modifying
    @Query("UPDATE PushDeviceToken d SET d.active = false WHERE d.id = :id")
    int deactivateById(@Param("id") Long id);

    long countByActiveTrue();
}
