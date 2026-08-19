package com.bialem.backend.repository;

import com.bialem.backend.domain.EventMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EventMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventMessageRepository extends JpaRepository<EventMessage, Long> {}
