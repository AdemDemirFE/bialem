package com.bialem.backend.repository;

import com.bialem.backend.domain.Hashtag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Hashtag entity.
 */
@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long>, JpaSpecificationExecutor<Hashtag> {
    Optional<Hashtag> findByNormalizedName(String normalizedName);
}
