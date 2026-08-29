package com.bialem.backend.payment;

import com.bialem.backend.domain.Order;
import com.bialem.backend.domain.Payment;

/**
 * Abstraction for payment providers (iyzico, Stripe, etc.).
 */
public interface PaymentProvider {

    /**
     * Start a payment and return provider-specific data the client needs
     * to complete the checkout (e.g. a checkout form URL or token).
     */
    PaymentInitiationResult initiatePayment(Payment payment, Order order, String callbackUrl);

    /**
     * Verify a provider callback/webhook payload and return the verified status.
     * Implementations must be idempotent and must validate signatures when available.
     */
    PaymentVerificationResult verifyCallback(String providerTransactionId, String payload);

    /**
     * Provider type this implementation handles.
     */
    com.bialem.backend.domain.enumeration.PaymentProviderType getType();
}
