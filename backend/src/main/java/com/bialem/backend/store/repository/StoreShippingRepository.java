package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreShipping;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreShippingRepository extends JpaRepository<StoreShipping, Long> {
    Optional<StoreShipping> findByOrderId(Long orderId);

    Optional<StoreShipping> findByTrackingNumber(String trackingNumber);
}
