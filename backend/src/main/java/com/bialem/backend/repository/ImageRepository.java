package com.bialem.backend.repository;

import com.bialem.backend.domain.Image;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Image entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findFirstByChecksum(String checksum);
}
