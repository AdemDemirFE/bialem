package com.bialem.backend.repository;

import com.bialem.backend.domain.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_OrderNumber(String orderNumber);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByProviderTransactionId(String providerTransactionId);
}
