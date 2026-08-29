package com.bialem.backend.payment;

import com.bialem.backend.domain.Order;
import com.bialem.backend.domain.Payment;
import com.bialem.backend.domain.enumeration.PaymentProviderType;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stripe payment provider adapter.
 * Structural stub for future Stripe integration.
 */
@Component
public class StripePaymentProvider implements PaymentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(StripePaymentProvider.class);

    @Override
    public PaymentInitiationResult initiatePayment(Payment payment, Order order, String callbackUrl) {
        LOG.info("Initiating Stripe payment for order {}", order.getOrderNumber());
        String providerTransactionId = "stripe-" + UUID.randomUUID();
        return PaymentInitiationResult.success(
            providerTransactionId,
            callbackUrl + "?transactionId=" + providerTransactionId,
            providerTransactionId,
            Map.of("provider", "stripe")
        );
    }

    @Override
    public PaymentVerificationResult verifyCallback(String providerTransactionId, String payload) {
        LOG.info("Verifying Stripe callback for transaction {}", providerTransactionId);
        return PaymentVerificationResult.success(providerTransactionId, null, "USD");
    }

    @Override
    public PaymentProviderType getType() {
        return PaymentProviderType.STRIPE;
    }
}
