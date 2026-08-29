package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreReviewImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreReviewImageRepository extends JpaRepository<StoreReviewImage, Long> {
    List<StoreReviewImage> findByReviewIdOrderBySortOrderAsc(Long reviewId);
}
