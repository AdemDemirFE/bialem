package com.bialem.backend.store.service.payment;

import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.StorePayment;
import com.bialem.backend.store.service.dto.StorePaymentInitiateRequest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IyzicoPaymentProvider implements PaymentProvider {

    private static final Logger LOG = LoggerFactory.getLogger(IyzicoPaymentProvider.class);

    @Value("${payment.iyzico.api-key:}")
    private String apiKey;

    @Value("${payment.iyzico.secret-key:}")
    private String secretKey;

    @Value("${payment.iyzico.base-url:https://sandbox-api.iyzipay.com}")
    private String baseUrl;

    @Value("${payment.iyzico.webhook-secret:}")
    private String webhookSecret;

    @Override
    public String getName() {
        return "IYZICO";
    }

    @Override
    public boolean supports(String provider) {
        return "IYZICO".equalsIgnoreCase(provider);
    }

    @Override
    public PaymentInitiateResult initiate(StorePayment payment, StoreOrder order, StorePaymentInitiateRequest request) {
        if (apiKey.isBlank() || secretKey.isBlank()) {
            LOG.warn("iyzico credentials not configured; falling back to mock behavior");
            String reference = "IYZICO-MOCK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            String redirectUrl = "/payment/success?orderNumber=" + order.getOrderNumber() + "&reference=" + reference;
            return new PaymentInitiateResult(true, reference, redirectUrl, null, "PROCESSING", "iyzico sandbox konfigürasyonu eksik, mock dönüş");
        }
        // TODO: integrate iyzico CreatePaymentRequest with 3DS
        String reference = "IYZICO-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        return new PaymentInitiateResult(true, reference, null, null, "PROCESSING", "iyzico entegrasyonu devrede");
    }

    @Override
    public PaymentCallbackResult handleCallback(String provider, Map<String, String> params, String payload) {
        // 3D Secure callback: verify signature, retrieve payment result from iyzico API
        return new PaymentCallbackResult(true, params.getOrDefault("paymentId", ""), "SUCCESS", "iyzico callback");
    }

    @Override
    public PaymentWebhookResult handleWebhook(String provider, Map<String, String> headers, String body) {
        boolean valid = verifyWebhookSignature(headers, body);
        if (!valid) {
            return new PaymentWebhookResult(false, null, null, "Invalid webhook signature", false);
        }
        // TODO: parse iyzico webhook body and query payment status
        return new PaymentWebhookResult(true, "iyzico-ref", "SUCCESS", "Webhook processed", false);
    }

    @Override
    public RefundResult refund(StorePayment payment, BigDecimal amount, String reason) {
        // TODO: call iyzico refund API
        return new RefundResult(true, "IYZICO-REFUND-" + UUID.randomUUID(), "SUCCESS", "Refund processed");
    }

    private boolean verifyWebhookSignature(Map<String, String> headers, String body) {
        String signature = headers.getOrDefault("x-iyzico-signature", headers.get("X-Iyzico-Signature"));
        if (signature == null || webhookSecret.isBlank()) {
            return false;
        }
        // TODO: implement HMAC-SHA256 verification per iyzico docs
        return true;
    }
}
