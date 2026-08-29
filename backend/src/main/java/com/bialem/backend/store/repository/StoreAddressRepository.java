package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreAddress;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreAddressRepository extends JpaRepository<StoreAddress, Long> {
    List<StoreAddress> findByUserIdOrderByIsDefaultDescCreatedAtDesc(Long userId);

    Optional<StoreAddress> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}
