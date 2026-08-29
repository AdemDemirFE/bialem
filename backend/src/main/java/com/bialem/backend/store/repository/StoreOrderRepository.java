package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreOrderRepository extends JpaRepository<StoreOrder, Long>, JpaSpecificationExecutor<StoreOrder> {
    Optional<StoreOrder> findByOrderNumber(String orderNumber);

    Optional<StoreOrder> findByIdAndUserId(Long id, Long userId);

    Page<StoreOrder> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<StoreOrder> findByUserIdAndOrderStatusOrderByCreatedAtDesc(Long userId, StoreOrderStatus orderStatus, Pageable pageable);

    Page<StoreOrder> findByOrderStatusOrderByCreatedAtDesc(StoreOrderStatus orderStatus, Pageable pageable);
}
