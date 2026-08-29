package com.bialem.backend.store.service.dto;

import com.bialem.backend.store.domain.enumeration.StoreShippingStatus;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

public class StoreShippingDTO implements Serializable {

    private Long id;
    private String carrier;
    private String trackingNumber;
    private StoreShippingStatus shippingStatus;
    private Instant shippedAt;
    private LocalDate estimatedDeliveryDate;
    private Instant deliveredAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
