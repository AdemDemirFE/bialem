package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreCartItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCartItemRepository extends JpaRepository<StoreCartItem, Long> {
    List<StoreCartItem> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<StoreCartItem> findByIdAndUserId(Long id, Long userId);

    Optional<StoreCartItem> findByUserIdAndProductIdAndVariantId(Long userId, Long productId, Long variantId);

    void deleteByUserIdAndId(Long userId, Long id);

    void deleteByUserId(Long userId);
}
