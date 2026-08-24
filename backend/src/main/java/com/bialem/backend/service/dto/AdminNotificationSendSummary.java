package com.bialem.backend.service.dto;

public record AdminNotificationSendSummary(
    boolean notificationCreated,
    int recipientCount,
    long pushEligibleCount,
    int successCount,
    int failureCount,
    long withoutTokenCount,
    String status
) {}
