package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StorePaymentWebhook;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorePaymentWebhookRepository extends JpaRepository<StorePaymentWebhook, Long> {
    List<StorePaymentWebhook> findByPaymentId(Long paymentId);
}
