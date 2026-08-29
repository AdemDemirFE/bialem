package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StorePayment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorePaymentRepository extends JpaRepository<StorePayment, Long> {
    Optional<StorePayment> findByOrderId(Long orderId);

    Optional<StorePayment> findByIdempotencyKey(String idempotencyKey);

    Optional<StorePayment> findByTransactionId(String transactionId);
}
