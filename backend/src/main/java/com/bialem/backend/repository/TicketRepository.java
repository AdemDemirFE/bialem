package com.bialem.backend.repository;

import com.bialem.backend.domain.Ticket;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Ticket entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUser_Id(Long userId);

    List<Ticket> findByEvent_Id(Long eventId);

    Optional<Ticket> findByTicketCode(String ticketCode);
}
