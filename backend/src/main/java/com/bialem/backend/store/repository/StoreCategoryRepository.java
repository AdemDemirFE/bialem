package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Long> {
    List<StoreCategory> findByParentIsNullAndDeletedAtIsNullOrderBySortOrderAsc();

    List<StoreCategory> findByParentIdAndDeletedAtIsNullOrderBySortOrderAsc(Long parentId);

    Optional<StoreCategory> findBySlugAndDeletedAtIsNull(String slug);

    List<StoreCategory> findByIsActiveTrueAndDeletedAtIsNullOrderBySortOrderAsc();
}
