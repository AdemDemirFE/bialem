package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreBrand;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBrandRepository extends JpaRepository<StoreBrand, Long> {
    List<StoreBrand> findByIsActiveTrueAndDeletedAtIsNullOrderByNameAsc();

    Optional<StoreBrand> findBySlugAndDeletedAtIsNull(String slug);
}
