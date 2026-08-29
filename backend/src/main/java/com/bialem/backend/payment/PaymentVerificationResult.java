package com.bialem.backend.payment;

import java.math.BigDecimal;

/**
 * Result of verifying a provider callback/webhook.
 */
public record PaymentVerificationResult(
    boolean verified,
    String providerTransactionId,
    BigDecimal amount,
    String currency,
    String status,
    String failureReason
) {
    public static PaymentVerificationResult success(String providerTransactionId, BigDecimal amount, String currency) {
        return new PaymentVerificationResult(true, providerTransactionId, amount, currency, "COMPLETED", null);
    }

    public static PaymentVerificationResult failure(String providerTransactionId, String failureReason) {
        return new PaymentVerificationResult(false, providerTransactionId, null, null, "FAILED", failureReason);
    }
}
