package com.bialem.backend.repository;

import com.bialem.backend.domain.AccountPreferences;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AccountPreferences entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AccountPreferencesRepository extends JpaRepository<AccountPreferences, Long> {
    Optional<AccountPreferences> findOneByProfile_Id(Long id);
}
