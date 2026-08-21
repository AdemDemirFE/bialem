package com.bialem.backend.repository;

import com.bialem.backend.domain.NotificationOutbox;
import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationOutboxRepository extends JpaRepository<NotificationOutbox, Long>, JpaSpecificationExecutor<NotificationOutbox> {
    Optional<NotificationOutbox> findByIdempotencyKey(String idempotencyKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM NotificationOutbox o WHERE o.id = :id")
    Optional<NotificationOutbox> findByIdWithLock(@Param("id") Long id);

    @Query(
        value = "SELECT o FROM NotificationOutbox o WHERE o.status = :status AND (o.scheduledAt IS NULL OR o.scheduledAt <= :now) AND (o.nextAttemptAt IS NULL OR o.nextAttemptAt <= :now) ORDER BY o.createdAt ASC",
        countQuery = "SELECT COUNT(o) FROM NotificationOutbox o WHERE o.status = :status AND (o.scheduledAt IS NULL OR o.scheduledAt <= :now) AND (o.nextAttemptAt IS NULL OR o.nextAttemptAt <= :now)"
    )
    Page<NotificationOutbox> findPendingReadyForProcessing(
        @Param("status") NotificationOutboxStatus status,
        @Param("now") Instant now,
        Pageable pageable
    );

    @Modifying
    @Query(
        "UPDATE NotificationOutbox o SET o.status = :status, o.lastError = :error, o.nextAttemptAt = :nextAttemptAt, o.attemptCount = o.attemptCount + 1 WHERE o.id = :id"
    )
    int updateRetryState(
        @Param("id") Long id,
        @Param("status") NotificationOutboxStatus status,
        @Param("error") String error,
        @Param("nextAttemptAt") Instant nextAttemptAt
    );

    @Modifying
    @Query("UPDATE NotificationOutbox o SET o.status = :status, o.processedAt = :processedAt, o.sentAt = :sentAt WHERE o.id = :id")
    int updateFinalState(
        @Param("id") Long id,
        @Param("status") NotificationOutboxStatus status,
        @Param("processedAt") Instant processedAt,
        @Param("sentAt") Instant sentAt
    );

    List<NotificationOutbox> findByStatusAndScheduledAtBefore(NotificationOutboxStatus status, Instant scheduledAt);

    long countByStatus(NotificationOutboxStatus status);

    @Modifying
    @Query("DELETE FROM NotificationOutbox o WHERE o.notification.id = :notificationId")
    int deleteByNotification_Id(@Param("notificationId") Long notificationId);
}
