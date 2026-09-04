package com.bialem.backend.repository;

import com.bialem.backend.domain.RadioConfig;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RadioConfig entity.
 */
@Repository
public interface RadioConfigRepository extends JpaRepository<RadioConfig, Long> {
    Optional<RadioConfig> findFirstByOrderByCreatedAtDesc();
}
