package com.bialem.backend.store.service.payment;

import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.StorePayment;
import com.bialem.backend.store.service.dto.StorePaymentInitiateRequest;
import java.math.BigDecimal;
import java.util.Map;

public interface PaymentProvider {
    String getName();

    PaymentInitiateResult initiate(StorePayment payment, StoreOrder order, StorePaymentInitiateRequest request);

    PaymentCallbackResult handleCallback(String provider, Map<String, String> params, String payload);

    PaymentWebhookResult handleWebhook(String provider, Map<String, String> headers, String body);

    RefundResult refund(StorePayment payment, BigDecimal amount, String reason);

    boolean supports(String provider);

    record PaymentInitiateResult(
        boolean success,
        String transactionReference,
        String redirectUrl,
        String htmlContent,
        String status,
        String message
    ) {}

    record PaymentCallbackResult(boolean success, String providerReference, String status, String message) {}

    record PaymentWebhookResult(boolean success, String providerReference, String status, String message, boolean duplicate) {}

    record RefundResult(boolean success, String providerReference, String status, String message) {}
}
