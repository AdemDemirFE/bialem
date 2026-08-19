package com.bialem.backend.repository;

import com.bialem.backend.domain.EventRating;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventRating entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRatingRepository extends JpaRepository<EventRating, Long> {}
