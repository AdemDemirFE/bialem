package com.bialem.backend.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_payment_webhook")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StorePaymentWebhook implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column(name = "provider", nullable = false, length = 120)
    private String provider;

    @NotNull
    @Size(max = 255)
    @Column(name = "event_type", nullable = false, length = 255)
    private String eventType;

    @Size(max = 255)
    @Column(name = "provider_reference", length = 255)
    private String providerReference;

    @Column(name = "payload", columnDefinition = "text")
    private String payload;

    @Size(max = 1000)
    @Column(name = "signature", length = 1000)
    private String signature;

    @NotNull
    @Column(name = "signature_valid", nullable = false)
    private Boolean signatureValid = false;

    @NotNull
    @Column(name = "processed", nullable = false)
    private Boolean processed = false;

    @Size(max = 1000)
    @Column(name = "processing_error", length = 1000)
    private String processingError;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private StorePayment payment;

    @NotNull
    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getProviderReference() {
        return providerReference;
    }

    public void setProviderReference(String providerReference) {
        this.providerReference = providerReference;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean getSignatureValid() {
        return signatureValid;
    }

    public void setSignatureValid(Boolean signatureValid) {
        this.signatureValid = signatureValid;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public String getProcessingError() {
        return processingError;
    }

    public void setProcessingError(String processingError) {
        this.processingError = processingError;
    }

    public StorePayment getPayment() {
        return payment;
    }

    public void setPayment(StorePayment payment) {
        this.payment = payment;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }
}
