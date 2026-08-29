package com.bialem.backend.store.domain.enumeration;

public enum StoreOrderStatus {
    PENDING_PAYMENT,
    PAID,
    WAITING_ADMIN_APPROVAL,
    APPROVED,
    PREPARING,
    READY_FOR_SHIPPING,
    SHIPPED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    PAYMENT_FAILED,
    RETURN_REQUESTED,
    RETURNED,
    REFUNDED,
}
