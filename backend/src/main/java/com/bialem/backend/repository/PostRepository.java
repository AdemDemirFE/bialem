package com.bialem.backend.repository;

import com.bialem.backend.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Post entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(
        value = "select distinct p from Post p left join fetch p.author left join fetch p.community left join fetch p.event left join fetch p.media where (:authorId is null or p.author.id = :authorId) and (:communityId is null or p.community.id = :communityId) and (:eventId is null or p.event.id = :eventId)",
        countQuery = "select count(p) from Post p where (:authorId is null or p.author.id = :authorId) and (:communityId is null or p.community.id = :communityId) and (:eventId is null or p.event.id = :eventId)"
    )
    Page<Post> findAllWithRelationshipsByFilters(
        @Param("authorId") Long authorId,
        @Param("communityId") Long communityId,
        @Param("eventId") Long eventId,
        Pageable pageable
    );

    @Query("select count(p) from Post p where (:authorId is null or p.author.id = :authorId) and (:communityId is null or p.community.id = :communityId) and (:eventId is null or p.event.id = :eventId)")
    long countByFilters(
        @Param("authorId") Long authorId,
        @Param("communityId") Long communityId,
        @Param("eventId") Long eventId
    );
}
