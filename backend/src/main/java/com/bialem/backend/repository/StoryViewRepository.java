package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryView;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoryView entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StoryViewRepository extends JpaRepository<StoryView, Long> {}
