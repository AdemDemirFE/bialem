package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreReview;
import com.bialem.backend.store.domain.enumeration.StoreReviewStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreReviewRepository extends JpaRepository<StoreReview, Long> {
    Page<StoreReview> findByProductIdAndStatusOrderByCreatedAtDesc(Long productId, StoreReviewStatus status, Pageable pageable);

    Page<StoreReview> findByProductIdAndStatusAndRatingOrderByCreatedAtDesc(Long productId, StoreReviewStatus status, Integer rating, Pageable pageable);

    Optional<StoreReview> findByIdAndUserId(Long id, Long userId);

    Optional<StoreReview> findByUserIdAndOrderItemId(Long userId, Long orderItemId);

    boolean existsByUserIdAndOrderItemId(Long userId, Long orderItemId);

    @Query(
        "SELECT COALESCE(AVG(r.rating), 0), COALESCE(COUNT(r), 0) FROM StoreReview r WHERE r.product.id = :productId AND r.status = :status"
    )
    Object[][] aggregateRatingByProductIdAndStatus(@Param("productId") Long productId, @Param("status") StoreReviewStatus status);

    @Query(
        "SELECT r.rating, COUNT(r) FROM StoreReview r WHERE r.product.id = :productId AND r.status = :status GROUP BY r.rating"
    )
    Object[][] countByProductIdAndStatusGroupByRating(@Param("productId") Long productId, @Param("status") StoreReviewStatus status);
}
