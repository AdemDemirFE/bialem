package com.bialem.backend.repository;

import com.bialem.backend.domain.EventTicket;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventTicket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventTicketRepository extends JpaRepository<EventTicket, Long> {
    List<EventTicket> findByEvent_Id(Long eventId);
}
