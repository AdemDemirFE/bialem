package com.bialem.backend.store.repository;

import com.bialem.backend.store.domain.StoreCoupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreCouponRepository extends JpaRepository<StoreCoupon, Long> {
    Optional<StoreCoupon> findByCodeAndIsActiveTrue(String code);
}
