package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryReaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoryReaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoryReactionRepository extends JpaRepository<StoryReaction, Long> {
    Optional<StoryReaction> findByStory_IdAndUser_Id(Long storyId, Long userId);

    long countByStory_Id(Long storyId);
}
