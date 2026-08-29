package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreProductImageRepository extends JpaRepository<StoreProductImage, Long> {
    List<StoreProductImage> findByProductIdOrderBySortOrderAsc(Long productId);
}
