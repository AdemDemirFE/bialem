package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.NotificationPriority;
import com.bialem.backend.domain.enumeration.NotificationTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record AdminNotificationSendRequest(
    @NotBlank @Size(max = 200) String title,
    @NotBlank @Size(max = 2000) String body,
    @NotNull NotificationTargetType targetType,
    Long userId,
    String role,
    Long communityId,
    Long eventId,
    @Size(max = 100) String city,
    @Size(max = 80) String notificationType,
    @Size(max = 500) String route,
    @Size(max = 80) String referenceType,
    @Size(max = 120) String referenceId,
    Boolean sendPush,
    Boolean inAppEnabled,
    Instant scheduledAt,
    NotificationPriority priority,
    @Size(max = 120) String requestId
) {}
