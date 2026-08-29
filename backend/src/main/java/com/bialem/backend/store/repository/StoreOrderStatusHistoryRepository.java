package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreOrderStatusHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreOrderStatusHistoryRepository extends JpaRepository<StoreOrderStatusHistory, Long> {
    List<StoreOrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
