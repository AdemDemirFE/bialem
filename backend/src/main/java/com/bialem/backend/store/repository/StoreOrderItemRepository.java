package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreOrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreOrderItemRepository extends JpaRepository<StoreOrderItem, Long> {
    List<StoreOrderItem> findByOrderId(Long orderId);

    boolean existsByOrderIdAndProductId(Long orderId, Long productId);
}
