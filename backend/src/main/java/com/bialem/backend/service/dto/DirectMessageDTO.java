package com.bialem.backend.service.dto;

import java.time.Instant;

public record DirectMessageDTO(Long id, Long conversationId, Long senderProfileId, String body, Instant createdAt, Instant readAt) {}
