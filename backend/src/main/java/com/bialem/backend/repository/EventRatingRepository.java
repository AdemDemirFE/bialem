package com.bialem.backend.repository;

import com.bialem.backend.domain.EventRating;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventRating entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRatingRepository extends JpaRepository<EventRating, Long> {
    List<EventRating> findByEvent_IdOrderByCreatedAtDesc(Long eventId);

    List<EventRating> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
