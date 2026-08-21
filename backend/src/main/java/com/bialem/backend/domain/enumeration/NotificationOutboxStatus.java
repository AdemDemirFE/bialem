package com.bialem.backend.domain.enumeration;

public enum NotificationOutboxStatus {
    PENDING,
    PROCESSING,
    SENT,
    PARTIAL,
    FAILED,
    CANCELLED,
    SKIPPED,
}
