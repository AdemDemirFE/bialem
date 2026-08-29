package com.bialem.backend.store.service.dto;

import jakarta.validation.constraints.*;

public class StorePaymentCallbackRequest {

    @NotBlank
    private String orderNumber;

    @NotBlank
    private String providerTransactionId;

    @NotBlank
    private String status;

    private String payload;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProviderTransactionId() {
        return providerTransactionId;
    }

    public void setProviderTransactionId(String providerTransactionId) {
        this.providerTransactionId = providerTransactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
