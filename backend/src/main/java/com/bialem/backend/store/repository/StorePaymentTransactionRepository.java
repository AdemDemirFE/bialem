package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StorePaymentTransaction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorePaymentTransactionRepository extends JpaRepository<StorePaymentTransaction, Long> {
    Optional<StorePaymentTransaction> findByTransactionReference(String transactionReference);
}
