package com.bialem.backend.store.domain;

import com.bialem.backend.domain.Profile;
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
@Table(name = "store_payment_refund")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StorePaymentRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column(name = "refund_reference", nullable = false, length = 120, unique = true)
    private String refundReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StorePaymentStatus status = StorePaymentStatus.PENDING;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Size(max = 255)
    @Column(name = "provider_reference", length = 255)
    private String providerReference;

    @Column(name = "provider_response", columnDefinition = "text")
    private String providerResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private StorePayment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile approvedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

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

    public String getRefundReference() {
        return refundReference;
    }

    public void setRefundReference(String refundReference) {
        this.refundReference = refundReference;
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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public StorePayment getPayment() {
        return payment;
    }

    public void setPayment(StorePayment payment) {
        this.payment = payment;
    }

    public Profile getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Profile approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
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
