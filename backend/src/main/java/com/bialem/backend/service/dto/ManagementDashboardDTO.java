package com.bialem.backend.service.dto;

public record ManagementDashboardDTO(
    Users users, Communities communities, Events events, Moderation moderation, Communications communications
) {
    public record Users(long total, long active, long inactive, long suspended, long newToday, long newThisWeek) {}
    public record Communities(long total, long active, long pendingRequests) {}
    public record Events(long total, long upcoming, long pendingApproval) {}
    public record Moderation(long openReports, long flaggedPosts, long flaggedComments) {}
    public record Communications(long notificationsSent) {}
}
