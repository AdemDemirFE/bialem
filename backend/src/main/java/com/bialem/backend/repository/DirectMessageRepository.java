package com.bialem.backend.repository;

import com.bialem.backend.domain.DirectMessage;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
    Optional<DirectMessage> findFirstByConversation_IdOrderByCreatedAtDesc(Long conversationId);
    long countByConversation_IdAndSender_IdNotAndReadAtIsNull(Long conversationId, Long profileId);

    @Modifying
    @Query("update DirectMessage m set m.readAt = :readAt where m.conversation.id = :conversationId and m.sender.id <> :profileId and m.readAt is null")
    int markRead(@Param("conversationId") Long conversationId, @Param("profileId") Long profileId, @Param("readAt") Instant readAt);
}
