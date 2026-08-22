package com.bialem.backend.repository;

import com.bialem.backend.domain.CityEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import com.bialem.backend.domain.enumeration.CityEventStatus;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the CityEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityEventRepository extends JpaRepository<CityEvent, Long> {
    @Query("select e from CityEvent e where e.startsAt >= :start and e.startsAt < :end and e.status=:status and lower(e.city)=lower(:city) order by e.startsAt")
    List<CityEvent> findCalendarEvents(@Param("start") Instant start, @Param("end") Instant end, @Param("city") String city, @Param("status") CityEventStatus status);
}
