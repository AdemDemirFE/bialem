package com.bialem.backend.repository;

import com.bialem.backend.domain.AppNotification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AppNotification entity.
 */
@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, Long> {
    List<AppNotification> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Page<AppNotification> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<AppNotification> findByUser_IdAndIsReadOrderByCreatedAtDesc(Long userId, Boolean isRead, Pageable pageable);

    Optional<AppNotification> findByIdAndUser_Id(Long id, Long userId);

    Optional<AppNotification> findByIdempotencyKey(String idempotencyKey);

    long countByUser_IdAndIsReadFalse(Long userId);

    @Modifying
    @Query(
        "update AppNotification n set n.isRead = true, n.readAt = :readAt where n.user.id = :userId and n.isRead = false"
    )
    int markAllReadForUser(@Param("userId") Long userId, @Param("readAt") Instant readAt);
}
