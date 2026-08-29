package com.bialem.backend.store.service.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class StoreShippingRequest {

    @NotBlank
    @Size(max = 120)
    private String carrier;

    @NotBlank
    @Size(max = 200)
    private String trackingNumber;

    private LocalDate estimatedDeliveryDate;

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

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }
}
