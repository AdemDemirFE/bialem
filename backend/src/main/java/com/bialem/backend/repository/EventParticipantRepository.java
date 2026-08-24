package com.bialem.backend.repository;

import com.bialem.backend.domain.EventParticipant;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.repository.query.Param;
import com.bialem.backend.domain.enumeration.EventParticipantStatus;

/**
 * Spring Data JPA repository for the EventParticipant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    @Query("select p.event.id, count(p) from EventParticipant p where p.event.id in :ids and p.status in (com.bialem.backend.domain.enumeration.EventParticipantStatus.APPROVED, com.bialem.backend.domain.enumeration.EventParticipantStatus.CHECKED_IN) group by p.event.id")
    List<Object[]> countCalendarParticipants(@Param("ids") List<Long> ids);

    @Query("select distinct p.user.user from EventParticipant p where p.event.id = :eventId and p.status in :statuses and p.user.user.activated = true")
    List<com.bialem.backend.domain.User> findRecipientUsers(
        @Param("eventId") Long eventId,
        @Param("statuses") List<EventParticipantStatus> statuses
    );
}
