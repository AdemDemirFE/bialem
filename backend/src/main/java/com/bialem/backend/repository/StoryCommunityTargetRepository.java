package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryCommunityTarget;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the StoryCommunityTarget entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoryCommunityTargetRepository extends JpaRepository<StoryCommunityTarget, Long> {
    @Modifying
    @Transactional
    void deleteByStory_Id(Long storyId);
}
