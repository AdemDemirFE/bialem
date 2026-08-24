package com.bialem.backend.repository;

import com.bialem.backend.domain.CommunityModeratorAssistant;
import org.springframework.data.jpa.repository.*;
import java.util.Optional;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommunityModeratorAssistant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommunityModeratorAssistantRepository extends JpaRepository<CommunityModeratorAssistant, Long> {
    @Query("select a from CommunityModeratorAssistant a where a.community.id = :communityId and a.user.user.login = :login")
    Optional<CommunityModeratorAssistant> findForUser(@Param("communityId") Long communityId, @Param("login") String login);
}
