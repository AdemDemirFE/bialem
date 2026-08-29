package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoryHashtag entity.
 */
@Repository
public interface StoryHashtagRepository extends JpaRepository<StoryHashtag, Long>, JpaSpecificationExecutor<StoryHashtag> {
}
