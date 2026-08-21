package com.bialem.backend.repository;

import com.bialem.backend.domain.NotificationTemplate;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByEventType(NotificationEventType eventType);

    Optional<NotificationTemplate> findByCode(String code);
}
