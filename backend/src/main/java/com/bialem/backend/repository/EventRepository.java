package com.bialem.backend.repository;

import com.bialem.backend.domain.Event;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import com.bialem.backend.domain.enumeration.EventStatus;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Event entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @EntityGraph(attributePaths = {"community"})
    @Query("select e from Event e where e.startsAt >= :start and e.startsAt < :end and e.status = :status order by e.publishedToDiscovery desc, e.startsAt")
    List<Event> findCalendarEvents(@Param("start") Instant start, @Param("end") Instant end, @Param("status") EventStatus status);
}
