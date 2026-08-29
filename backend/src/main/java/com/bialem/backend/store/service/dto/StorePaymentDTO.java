package com.bialem.backend.store.service.dto;

import com.bialem.backend.store.domain.enumeration.StorePaymentMethod;
import com.bialem.backend.store.domain.enumeration.StorePaymentProviderType;
import com.bialem.backend.store.domain.enumeration.StorePaymentStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class StorePaymentDTO implements Serializable {

    private Long id;
    private StorePaymentProviderType provider;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private StorePaymentStatus status;
    private StorePaymentMethod paymentMethod;
    private Instant paidAt;
    private String failureReason;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StorePaymentProviderType getProvider() {
        return provider;
    }

    public void setProvider(StorePaymentProviderType provider) {
        this.provider = provider;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public StorePaymentStatus getStatus() {
        return status;
    }

    public void setStatus(StorePaymentStatus status) {
        this.status = status;
    }

    public StorePaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(StorePaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
