package com.bialem.backend.repository;

import com.bialem.backend.domain.CommunityModeratorAssistant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CommunityModeratorAssistant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommunityModeratorAssistantRepository extends JpaRepository<CommunityModeratorAssistant, Long> {}
