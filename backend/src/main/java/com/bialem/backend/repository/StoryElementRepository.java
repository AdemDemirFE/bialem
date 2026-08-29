package com.bialem.backend.repository;

import com.bialem.backend.domain.StoryElement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StoryElement entity.
 */
@Repository
public interface StoryElementRepository extends JpaRepository<StoryElement, Long>, JpaSpecificationExecutor<StoryElement> {
    List<StoryElement> findByStoryIdOrderBySortOrderAsc(Long storyId);
}
