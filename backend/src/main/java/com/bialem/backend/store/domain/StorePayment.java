package com.bialem.backend.store.domain;

import com.bialem.backend.store.domain.enumeration.StorePaymentMethod;
import com.bialem.backend.store.domain.enumeration.StorePaymentProviderType;
import com.bialem.backend.store.domain.enumeration.StorePaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(
    name = "store_payment",
    uniqueConstraints = { @UniqueConstraint(name = "ux_store_payment__idempotency_key", columnNames = { "idempotency_key" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StorePayment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private StorePaymentProviderType provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 80)
    private StorePaymentMethod paymentMethod;

    @Size(max = 255)
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "refunded_amount", precision = 21, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @NotNull
    @Size(max = 8)
    @Column(name = "currency", nullable = false, length = 8)
    private String currency = "TRY";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StorePaymentStatus status = StorePaymentStatus.PENDING;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Size(max = 1000)
    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Size(max = 4000)
    @Column(name = "provider_response", length = 4000)
    private String providerResponse;

    @NotNull
    @Size(max = 255)
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "callback_payload", columnDefinition = "text")
    private String callbackPayload;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    @JsonIgnoreProperties(value = { "items", "statusHistory", "payment", "shipping" }, allowSetters = true)
    private StoreOrder order;

    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
    private List<StorePaymentTransaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
    private List<StorePaymentRefund> refunds = new ArrayList<>();

    @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "payment" }, allowSetters = true)
    private List<StorePaymentWebhook> webhooks = new ArrayList<>();

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

    public StorePaymentProviderType getProvider() {
        return provider;
    }

    public void setProvider(StorePaymentProviderType provider) {
        this.provider = provider;
    }

    public StorePaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(StorePaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
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

    public String getProviderResponse() {
        return providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCallbackPayload() {
        return callbackPayload;
    }

    public void setCallbackPayload(String callbackPayload) {
        this.callbackPayload = callbackPayload;
    }

    public StoreOrder getOrder() {
        return order;
    }

    public void setOrder(StoreOrder order) {
        this.order = order;
    }

    public List<StorePaymentTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<StorePaymentTransaction> transactions) {
        this.transactions = transactions;
    }

    public List<StorePaymentRefund> getRefunds() {
        return refunds;
    }

    public void setRefunds(List<StorePaymentRefund> refunds) {
        this.refunds = refunds;
    }

    public List<StorePaymentWebhook> getWebhooks() {
        return webhooks;
    }

    public void setWebhooks(List<StorePaymentWebhook> webhooks) {
        this.webhooks = webhooks;
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
