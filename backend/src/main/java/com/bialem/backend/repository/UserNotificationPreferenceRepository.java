package com.bialem.backend.repository;

import com.bialem.backend.domain.UserNotificationPreference;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationPreferenceRepository extends JpaRepository<UserNotificationPreference, Long> {
    List<UserNotificationPreference> findByUser_Id(Long userId);

    Optional<UserNotificationPreference> findByUser_IdAndNotificationType(Long userId, String notificationType);

    boolean existsByUser_IdAndNotificationType(Long userId, String notificationType);
}
