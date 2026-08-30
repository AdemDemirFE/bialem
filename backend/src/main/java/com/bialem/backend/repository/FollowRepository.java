package com.bialem.backend.repository;

import com.bialem.backend.domain.Follow;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Follow entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollower_IdAndFollowed_Id(Long followerId, Long followedId);

    @Query("select f from Follow f where (:followerId is null or f.follower.id = :followerId) and (:followedId is null or f.followed.id = :followedId)")
    List<Follow> findAllByFollowerIdAndFollowedId(@Param("followerId") Long followerId, @Param("followedId") Long followedId);
}
