package com.bialem.backend.repository;

import com.bialem.backend.domain.UserReview;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserReview entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Long> {}
