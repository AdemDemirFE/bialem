package com.bialem.backend.service.dto;

import java.time.Instant;

public record DirectConversationDTO(
    Long id,
    Long otherProfileId,
    String displayName,
    String username,
    String avatarUrl,
    String lastMessage,
    Instant lastMessageAt,
    long unreadCount
) {}
