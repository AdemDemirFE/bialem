package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryGroup;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoryGroup entity.
 */
@Repository
public interface StoryGroupRepository extends JpaRepository<StoryGroup, Long>, JpaSpecificationExecutor<StoryGroup> {
    Optional<StoryGroup> findByAuthorIdAndEventIdAndCommunityIdAndExpiresAtAfter(Long authorId, Long eventId, Long communityId, Instant expiresAt);

    Optional<StoryGroup> findByAuthorIdAndEventIdIsNullAndCommunityIdIsNullAndExpiresAtAfter(Long authorId, Instant expiresAt);

    Optional<StoryGroup> findByAuthorIdAndEventIdAndCommunityIdIsNullAndExpiresAtAfter(Long authorId, Long eventId, Instant expiresAt);

    Optional<StoryGroup> findByAuthorIdAndEventIdIsNullAndCommunityIdAndExpiresAtAfter(Long authorId, Long communityId, Instant expiresAt);
}
