package com.bialem.backend.repository;

import com.bialem.backend.domain.CityEvent;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CityEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityEventRepository extends JpaRepository<CityEvent, Long> {}
