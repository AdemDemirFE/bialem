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
    Optional<PushDeviceToken> findByFirebaseInstallationId(String firebaseInstallationId);
    Optional<PushDeviceToken> findByDeviceUuid(String deviceUuid);

    List<PushDeviceToken> findByUser_Id(Long userId);

    List<PushDeviceToken> findByUser_IdAndActiveTrue(Long userId);

    @Modifying
    @Query("UPDATE PushDeviceToken d SET d.active = false WHERE d.lastSeenAt < :cutoff AND d.active = true")
    int deactivateStaleDevices(@Param("cutoff") Instant cutoff);

    @Modifying
    @Query("UPDATE PushDeviceToken d SET d.active = false WHERE d.id = :id")
    int deactivateById(@Param("id") Long id);

    long countByActiveTrue();

    long countByActiveTrueAndPlatform(com.bialem.backend.domain.enumeration.PushPlatform platform);

    @Query("select count(distinct d.user.id) from PushDeviceToken d where d.active = true and d.notificationsEnabled = true and d.user.id in :userIds")
    long countPushEligibleUsers(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query("update PushDeviceToken d set d.active = false, d.updatedAt = :now where d.user.id = :userId and " +
        "((:token is not null and d.token = :token) or (:deviceUuid is not null and d.deviceUuid = :deviceUuid) or " +
        "(:installationId is not null and d.firebaseInstallationId = :installationId))")
    int deactivateCurrentDevice(@Param("userId") Long userId, @Param("token") String token,
        @Param("deviceUuid") String deviceUuid, @Param("installationId") String installationId, @Param("now") Instant now);
}
