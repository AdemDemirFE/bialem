package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreProductVariant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreProductVariantRepository extends JpaRepository<StoreProductVariant, Long> {
    List<StoreProductVariant> findByProductIdAndIsActiveTrue(Long productId);

    Optional<StoreProductVariant> findByIdAndProductId(Long id, Long productId);
}
