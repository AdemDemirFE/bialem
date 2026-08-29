package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreProductAttribute;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreProductAttributeRepository extends JpaRepository<StoreProductAttribute, Long> {
    List<StoreProductAttribute> findByProductIdOrderBySortOrderAsc(Long productId);
}
