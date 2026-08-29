package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreWishlist;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreWishlistRepository extends JpaRepository<StoreWishlist, Long> {
    Page<StoreWishlist> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<StoreWishlist> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
