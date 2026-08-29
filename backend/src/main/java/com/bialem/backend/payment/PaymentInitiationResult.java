package com.bialem.backend.payment;

import java.util.Map;

/**
 * Result of initiating a payment with a provider.
 */
public record PaymentInitiationResult(
    boolean success,
    String providerTransactionId,
    String checkoutUrl,
    String clientToken,
    Map<String, Object> metadata,
    String errorMessage
) {
    public static PaymentInitiationResult success(String providerTransactionId, String checkoutUrl, String clientToken, Map<String, Object> metadata) {
        return new PaymentInitiationResult(true, providerTransactionId, checkoutUrl, clientToken, metadata, null);
    }

    public static PaymentInitiationResult failure(String errorMessage) {
        return new PaymentInitiationResult(false, null, null, null, null, errorMessage);
    }
}
