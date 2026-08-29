package com.bialem.backend.store.domain;

import com.bialem.backend.store.domain.enumeration.StorePaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_payment_transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StorePaymentTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column(name = "transaction_reference", nullable = false, length = 120)
    private String transactionReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StorePaymentStatus status = StorePaymentStatus.PENDING;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 21, scale = 2)
    private BigDecimal amount;

    @Size(max = 8)
    @Column(name = "currency", length = 8)
    private String currency = "TRY";

    @Size(max = 4000)
    @Column(name = "provider_request", length = 4000)
    private String providerRequest;

    @Column(name = "provider_response", columnDefinition = "text")
    private String providerResponse;

    @Size(max = 1000)
    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "processed_at")
    private Instant processedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private StorePayment payment;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public StorePaymentStatus getStatus() {
        return status;
    }

    public void setStatus(StorePaymentStatus status) {
        this.status = status;
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

    public String getProviderRequest() {
        return providerRequest;
    }

    public void setProviderRequest(String providerRequest) {
        this.providerRequest = providerRequest;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public StorePayment getPayment() {
        return payment;
    }

    public void setPayment(StorePayment payment) {
        this.payment = payment;
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
