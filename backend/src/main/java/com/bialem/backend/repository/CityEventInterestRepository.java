package com.bialem.backend.repository;

import com.bialem.backend.domain.CityEventInterest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CityEventInterest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CityEventInterestRepository extends JpaRepository<CityEventInterest, Long> {}
