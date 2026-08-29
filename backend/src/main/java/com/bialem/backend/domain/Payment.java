package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PaymentProviderType;
import com.bialem.backend.domain.enumeration.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Payment.
 */
@Entity
@Table(
    name = "payment",
    uniqueConstraints = { @UniqueConstraint(name = "ux_payment__idempotency_key", columnNames = { "idempotency_key" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private PaymentProviderType provider;

    @Column(name = "provider_transaction_id", length = 255)
    private String providerTransactionId;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 21, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency", nullable = false, length = 8)
    private String currency;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private Instant paymentDate;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "provider_response", length = 4000)
    private String providerResponse;

    @NotNull
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "callback_payload", length = 4000)
    private String callbackPayload;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private Order order;

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentProviderType getProvider() {
        return this.provider;
    }

    public Payment provider(PaymentProviderType provider) {
        this.setProvider(provider);
        return this;
    }

    public void setProvider(PaymentProviderType provider) {
        this.provider = provider;
    }

    public String getProviderTransactionId() {
        return this.providerTransactionId;
    }

    public Payment providerTransactionId(String providerTransactionId) {
        this.setProviderTransactionId(providerTransactionId);
        return this;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return this.currency;
    }

    public Payment currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public Payment status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Instant getPaymentDate() {
        return this.paymentDate;
    }

    public Payment paymentDate(Instant paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getFailureReason() {
        return this.failureReason;
    }

    public Payment failureReason(String failureReason) {
        this.setFailureReason(failureReason);
        return this;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getProviderResponse() {
        return this.providerResponse;
    }

    public Payment providerResponse(String providerResponse) {
        this.setProviderResponse(providerResponse);
        return this;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public Payment idempotencyKey(String idempotencyKey) {
        this.setIdempotencyKey(idempotencyKey);
        return this;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getCallbackPayload() {
        return this.callbackPayload;
    }

    public Payment callbackPayload(String callbackPayload) {
        this.setCallbackPayload(callbackPayload);
        return this;
    }

    public void setCallbackPayload(String callbackPayload) {
        this.callbackPayload = callbackPayload;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Payment order(Order order) {
        this.setOrder(order);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", provider='" + getProvider() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            "}";
    }
}
