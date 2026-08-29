package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreBankTransfer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBankTransferRepository extends JpaRepository<StoreBankTransfer, Long> {
    Optional<StoreBankTransfer> findByReferenceCode(String referenceCode);

    Optional<StoreBankTransfer> findByOrderId(Long orderId);
}
