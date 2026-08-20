package com.bialem.backend.repository;

import com.bialem.backend.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Notification> findByIdAndUser_Id(Long id, Long userId);

    long countByUser_IdAndIsReadFalse(Long userId);

    @Modifying
    @Query(
        "update Notification n set n.isRead = true, n.readAt = :readAt where n.user.id = :userId and n.isRead = false"
    )
    int markAllReadForUser(@Param("userId") Long userId, @Param("readAt") Instant readAt);
}
