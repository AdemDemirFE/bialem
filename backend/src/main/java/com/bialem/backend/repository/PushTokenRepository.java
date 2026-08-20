package com.bialem.backend.repository;

import com.bialem.backend.domain.PushToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PushToken entity.
 */
@Repository
public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByDeviceToken(String deviceToken);

    List<PushToken> findByUser_IdAndIsActiveTrue(Long userId);
}
