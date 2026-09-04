package com.bialem.backend.repository;

import com.bialem.backend.domain.RadioContent;
import com.bialem.backend.domain.enumeration.RadioContentType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RadioContent entity.
 */
@Repository
public interface RadioContentRepository extends JpaRepository<RadioContent, Long> {
    List<RadioContent> findByIsActiveTrueOrderBySortOrderAsc();

    List<RadioContent> findByIsFeaturedTrueAndIsActiveTrueOrderBySortOrderAsc();

    List<RadioContent> findByContentTypeAndIsActiveTrue(RadioContentType contentType);

    List<RadioContent> findByCategoryAndIsActiveTrueOrderBySortOrderAsc(String category);

    @Query("SELECT DISTINCT rc.category FROM RadioContent rc WHERE rc.category IS NOT NULL AND rc.isActive = true ORDER BY rc.category")
    List<String> findDistinctCategories();

    long countByIsActiveTrue();
}
