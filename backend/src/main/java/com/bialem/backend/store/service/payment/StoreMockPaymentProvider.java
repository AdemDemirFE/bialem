package com.bialem.backend.store.service.payment;

import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.StorePayment;
import com.bialem.backend.store.service.dto.StorePaymentInitiateRequest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class StoreMockPaymentProvider implements PaymentProvider {

    @Override
    public String getName() {
        return "MOCK";
    }

    @Override
    public boolean supports(String provider) {
        return "MOCK".equalsIgnoreCase(provider);
    }

    @Override
    public PaymentInitiateResult initiate(StorePayment payment, StoreOrder order, StorePaymentInitiateRequest request) {
        String reference = "MOCK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        boolean fail = request.getCardNumber() != null && request.getCardNumber().startsWith("4");
        if (fail) {
            return new PaymentInitiateResult(false, reference, null, null, "FAILED", "Kart reddedildi (mock)");
        }
        String redirectUrl = "/payment/success?orderNumber=" + order.getOrderNumber() + "&reference=" + reference;
        return new PaymentInitiateResult(true, reference, redirectUrl, null, "PROCESSING", "Mock ödeme başlatıldı");
    }

    @Override
    public PaymentCallbackResult handleCallback(String provider, Map<String, String> params, String payload) {
        String reference = params.getOrDefault("reference", "unknown");
        String status = params.getOrDefault("status", "SUCCESS");
        boolean success = "SUCCESS".equalsIgnoreCase(status);
        return new PaymentCallbackResult(success, reference, success ? "SUCCESS" : "FAILED", "Mock callback");
    }

    @Override
    public PaymentWebhookResult handleWebhook(String provider, Map<String, String> headers, String body) {
        return new PaymentWebhookResult(true, "mock-ref", "SUCCESS", "Mock webhook", false);
    }

    @Override
    public RefundResult refund(StorePayment payment, BigDecimal amount, String reason) {
        return new RefundResult(true, "REFUND-" + UUID.randomUUID(), "SUCCESS", "Mock iade başarılı");
    }
}
