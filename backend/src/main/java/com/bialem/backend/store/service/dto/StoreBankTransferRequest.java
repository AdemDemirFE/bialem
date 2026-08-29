package com.bialem.backend.store.service.dto;

import jakarta.validation.constraints.*;

public class StoreBankTransferRequest {

    @NotBlank
    private String orderNumber;

    @NotBlank
    private String receiptUrl;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }
}
