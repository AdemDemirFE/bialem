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
 * iyzico payment provider adapter.
 * This is a structural stub: integrate the official iyzico Java SDK here
 * and replace the stubbed initiate/verify logic with real API calls.
 */
@Component
public class IyzicoPaymentProvider implements PaymentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(IyzicoPaymentProvider.class);

    @Override
    public PaymentInitiationResult initiatePayment(Payment payment, Order order, String callbackUrl) {
        LOG.info("Initiating iyzico payment for order {}", order.getOrderNumber());
        String providerTransactionId = "iyzico-" + UUID.randomUUID();
        // TODO: call iyzico API (ThreedsInitialize / CheckoutFormInitialize)
        // For now return a checkout URL the mobile/web client can open.
        return PaymentInitiationResult.success(
            providerTransactionId,
            callbackUrl + "?transactionId=" + providerTransactionId,
            providerTransactionId,
            Map.of("provider", "iyzico")
        );
    }

    @Override
    public PaymentVerificationResult verifyCallback(String providerTransactionId, String payload) {
        LOG.info("Verifying iyzico callback for transaction {}", providerTransactionId);
        // TODO: validate iyzico signature and call iyzico API to confirm payment.
        return PaymentVerificationResult.success(providerTransactionId, null, "TRY");
    }

    @Override
    public PaymentProviderType getType() {
        return PaymentProviderType.IYZICO;
    }
}
