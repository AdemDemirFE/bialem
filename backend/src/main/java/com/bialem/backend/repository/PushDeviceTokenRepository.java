package com.bialem.backend.repository;

import com.bialem.backend.domain.PushDeviceToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PushDeviceToken entity.
 */
@Repository
public interface PushDeviceTokenRepository extends JpaRepository<PushDeviceToken, Long> {
    Optional<PushDeviceToken> findByToken(String token);

    List<PushDeviceToken> findByUser_Id(Long userId);
}
