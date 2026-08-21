package com.bialem.backend.repository;

import com.bialem.backend.domain.NotificationDeliveryLog;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDeliveryLogRepository extends JpaRepository<NotificationDeliveryLog, Long> {
    List<NotificationDeliveryLog> findByNotification_Id(Long notificationId);
}
