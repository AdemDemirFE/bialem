package com.bialem.backend.domain.enumeration;

/**
 * Supported notification event types.
 */
public enum NotificationEventType {
    NEW_FOLLOWER,
    EVENT_PUBLISHED,
    COMMUNITY_MEMBERSHIP_REQUEST,
    COMMUNITY_MEMBERSHIP_APPROVED,
    COMMUNITY_MEMBERSHIP_REJECTED,
    HONOR_BADGE_AWARDED,
    USER_REVIEW,
    SYSTEM_ANNOUNCEMENT,
    ADMIN_BROADCAST,
    EVENT_START_REMINDER,
}
