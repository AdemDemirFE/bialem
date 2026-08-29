package com.bialem.backend.store.domain;

import com.bialem.backend.store.domain.enumeration.StoreShippingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_shipping")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StoreShipping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    @JsonIgnoreProperties(value = { "items", "statusHistory", "payment", "shipping" }, allowSetters = true)
    private StoreOrder order;

    @NotNull
    @Size(max = 120)
    @Column(name = "carrier", nullable = false, length = 120)
    private String carrier;

    @NotNull
    @Size(max = 200)
    @Column(name = "tracking_number", nullable = false, length = 200)
    private String trackingNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false)
    private StoreShippingStatus shippingStatus = StoreShippingStatus.PENDING;

    @Column(name = "shipped_at")
    private Instant shippedAt;

    @Column(name = "estimated_delivery_date")
    private LocalDate estimatedDeliveryDate;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Size(max = 2000)
    @Column(name = "carrier_response", length = 2000)
    private String carrierResponse;

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

    public StoreOrder getOrder() {
        return order;
    }

    public void setOrder(StoreOrder order) {
        this.order = order;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public StoreShippingStatus getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(StoreShippingStatus shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Instant getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(Instant shippedAt) {
        this.shippedAt = shippedAt;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public Instant getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Instant deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getCarrierResponse() {
        return carrierResponse;
    }

    public void setCarrierResponse(String carrierResponse) {
        this.carrierResponse = carrierResponse;
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
