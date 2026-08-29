package com.bialem.backend.store.service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class StoreRefundRequest {

    @NotBlank
    private String orderNumber;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    private String reason;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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
}
