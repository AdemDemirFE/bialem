package com.bialem.backend.repository;

import com.bialem.backend.domain.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Id(Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);
}
