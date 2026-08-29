package com.bialem.backend.store.service.dto;

import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class StoreOrderDTO implements Serializable {

    private Long id;
    private String orderNumber;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    private String currency;
    private StoreOrderStatus paymentStatus;
    private StoreOrderStatus orderStatus;
    private StoreOrderStatus shippingStatus;
    private String customerNote;
    private String couponCode;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public StoreOrderStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(StoreOrderStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public StoreOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(StoreOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public StoreOrderStatus getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(StoreOrderStatus shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
