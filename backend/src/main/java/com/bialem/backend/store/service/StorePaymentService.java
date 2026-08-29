package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.domain.*;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import com.bialem.backend.store.domain.enumeration.StorePaymentMethod;
import com.bialem.backend.store.domain.enumeration.StorePaymentProviderType;
import com.bialem.backend.store.domain.enumeration.StorePaymentStatus;
import com.bialem.backend.store.repository.*;
import com.bialem.backend.store.service.dto.*;
import com.bialem.backend.store.service.mapper.StoreMapper;
import com.bialem.backend.store.service.payment.PaymentProvider;
import com.bialem.backend.store.service.payment.PaymentProviderFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StorePaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(StorePaymentService.class);

    private final StoreOrderRepository orderRepository;
    private final StorePaymentRepository paymentRepository;
    private final StorePaymentTransactionRepository transactionRepository;
    private final StorePaymentRefundRepository refundRepository;
    private final StoreBankTransferRepository bankTransferRepository;
    private final StorePaymentWebhookRepository webhookRepository;
    private final StoreOrderStatusHistoryRepository statusHistoryRepository;
    private final StoreProductRepository productRepository;
    private final StoreCartItemRepository cartItemRepository;
    private final StoreMapper mapper;
    private final AppSupport appSupport;
    private final PaymentProviderFactory providerFactory;
    private final NotificationEventPublisher eventPublisher;

    public StorePaymentService(
        StoreOrderRepository orderRepository,
        StorePaymentRepository paymentRepository,
        StorePaymentTransactionRepository transactionRepository,
        StorePaymentRefundRepository refundRepository,
        StoreBankTransferRepository bankTransferRepository,
        StorePaymentWebhookRepository webhookRepository,
        StoreOrderStatusHistoryRepository statusHistoryRepository,
        StoreProductRepository productRepository,
        StoreCartItemRepository cartItemRepository,
        StoreMapper mapper,
        AppSupport appSupport,
        PaymentProviderFactory providerFactory,
        NotificationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.refundRepository = refundRepository;
        this.bankTransferRepository = bankTransferRepository;
        this.webhookRepository = webhookRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.mapper = mapper;
        this.appSupport = appSupport;
        this.providerFactory = providerFactory;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public StoreOrderDetailDTO getOrderForPayment(String orderNumber) {
        Profile profile = appSupport.currentProfile();
        StoreOrder order = orderRepository
            .findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu siparişe erişim yetkiniz yok");
        }
        return mapper.toOrderDetailDTO(order);
    }

    public StorePaymentInitiateResponse initiatePayment(StorePaymentInitiateRequest request) {
        Profile profile = appSupport.currentProfile();
        StoreOrder order = orderRepository
            .findByOrderNumber(request.getOrderNumber())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu siparişe erişim yetkiniz yok");
        }
        if (order.getOrderStatus() != StoreOrderStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sipariş için ödeme beklenmiyor");
        }

        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme kaydı bulunamadı"));

        StorePaymentMethod method = parsePaymentMethod(request.getPaymentMethod());
        payment.setPaymentMethod(method);
        payment.setStatus(StorePaymentStatus.PROCESSING);
        payment.setUpdatedAt(Instant.now());

        StorePaymentTransaction transaction = new StorePaymentTransaction();
        transaction.setPayment(payment);
        transaction.setTransactionReference("TX-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        transaction.setAmount(payment.getAmount());
        transaction.setCurrency(payment.getCurrency());
        transaction.setStatus(StorePaymentStatus.PROCESSING);
        transaction.setCreatedAt(Instant.now());
        transactionRepository.save(transaction);

        PaymentProvider provider = providerFactory.getProvider(payment.getProvider().name());
        PaymentProvider.PaymentInitiateResult result = provider.initiate(payment, order, request);

        transaction.setProviderRequest(maskSensitiveData(request));
        transaction.setProviderResponse(result.message());
        transaction.setStatus(parseStatus(result.status()));
        transaction.setProcessedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());
        if (result.transactionReference() != null) {
            transaction.setTransactionReference(result.transactionReference());
        }

        if (result.success()) {
            payment.setStatus(StorePaymentStatus.PROCESSING);
            payment.setTransactionId(result.transactionReference());
            if (method == StorePaymentMethod.CREDIT_CARD) {
                addOrderStatusHistory(order, StoreOrderStatus.PENDING_PAYMENT, "Kart ödemesi başlatıldı: " + result.transactionReference());
            }
        } else {
            payment.setStatus(StorePaymentStatus.FAILED);
            payment.setFailureReason(result.message());
            transaction.setStatus(StorePaymentStatus.FAILED);
            transaction.setFailureReason(result.message());
            transitionOrderStatus(order, StoreOrderStatus.PAYMENT_FAILED, "Ödeme başarısız: " + result.message());
        }

        paymentRepository.save(payment);

        StorePaymentInitiateResponse response = new StorePaymentInitiateResponse();
        response.setOrderNumber(order.getOrderNumber());
        response.setPaymentStatus(payment.getStatus().name());
        response.setRedirectUrl(result.redirectUrl());
        response.setHtmlContent(result.htmlContent());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setMessage(result.message());
        return response;
    }

    public StoreOrderDetailDTO handleCallback(String orderNumber, Map<String, String> params, String payload) {
        StoreOrder order = orderRepository
            .findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme bulunamadı"));

        PaymentProvider provider = providerFactory.getProvider(payment.getProvider().name());
        PaymentProvider.PaymentCallbackResult result = provider.handleCallback(payment.getProvider().name(), params, payload);

        payment.setCallbackPayload(payload);
        payment.setUpdatedAt(Instant.now());

        if (result.success()) {
            processPaymentSuccess(payment, order, result.providerReference());
        } else {
            processPaymentFailure(payment, order, result.message());
        }
        return mapper.toOrderDetailDTO(order);
    }

    public void handleWebhook(String providerName, Map<String, String> headers, String body) {
        StorePaymentWebhook webhook = new StorePaymentWebhook();
        webhook.setProvider(providerName);
        webhook.setPayload(body);
        webhook.setSignature(headers.getOrDefault("x-signature", headers.get("X-Signature")));
        webhook.setReceivedAt(Instant.now());
        webhook.setProcessed(false);

        PaymentProvider provider;
        try {
            provider = providerFactory.getProvider(providerName);
        } catch (IllegalArgumentException e) {
            webhook.setProcessingError("Bilinmeyen sağlayıcı");
            webhookRepository.save(webhook);
            return;
        }

        PaymentProvider.PaymentWebhookResult result = provider.handleWebhook(providerName, headers, body);
        webhook.setSignatureValid(true);
        webhook.setProcessed(result.success());
        if (result.success() && result.providerReference() != null) {
            paymentRepository
                .findByTransactionId(result.providerReference())
                .ifPresent(payment -> {
                    webhook.setPayment(payment);
                    StoreOrder order = payment.getOrder();
                    if (order != null && !result.duplicate()) {
                        if ("SUCCESS".equalsIgnoreCase(result.status())) {
                            processPaymentSuccess(payment, order, result.providerReference());
                        } else if ("FAILED".equalsIgnoreCase(result.status())) {
                            processPaymentFailure(payment, order, result.message());
                        }
                    }
                });
        }
        webhook.setProcessingError(result.message());
        webhook.setProcessedAt(Instant.now());
        webhookRepository.save(webhook);
    }

    public StoreOrderDetailDTO refund(StoreRefundRequest request) {
        Profile admin = appSupport.currentProfile();
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN", "ROLE_STORE_MANAGER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkiniz yok");
        }
        StoreOrder order = orderRepository
            .findByOrderNumber(request.getOrderNumber())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme bulunamadı"));

        BigDecimal alreadyRefunded = payment.getRefundedAmount() != null ? payment.getRefundedAmount() : BigDecimal.ZERO;
        BigDecimal available = payment.getAmount().subtract(alreadyRefunded);
        if (request.getAmount().compareTo(available) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "İade tutarı ödeme tutarından fazla olamaz");
        }

        StorePaymentRefund refund = new StorePaymentRefund();
        refund.setPayment(payment);
        refund.setRefundReference("REF-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        refund.setAmount(request.getAmount());
        refund.setReason(request.getReason());
        refund.setStatus(StorePaymentStatus.PROCESSING);
        refund.setApprovedBy(admin);
        refund.setApprovedAt(Instant.now());
        refund.setCreatedAt(Instant.now());
        refundRepository.save(refund);

        PaymentProvider provider = providerFactory.getProvider(payment.getProvider().name());
        PaymentProvider.RefundResult result = provider.refund(payment, request.getAmount(), request.getReason());

        refund.setProviderReference(result.providerReference());
        refund.setProviderResponse(result.message());
        refund.setStatus(result.success() ? StorePaymentStatus.SUCCESS : StorePaymentStatus.FAILED);
        refund.setUpdatedAt(Instant.now());

        if (result.success()) {
            BigDecimal newRefunded = alreadyRefunded.add(request.getAmount());
            payment.setRefundedAmount(newRefunded);
            if (newRefunded.compareTo(payment.getAmount()) >= 0) {
                payment.setStatus(StorePaymentStatus.REFUNDED);
                order.setPaymentStatus(StoreOrderStatus.REFUNDED);
            } else {
                payment.setStatus(StorePaymentStatus.PARTIALLY_REFUNDED);
                order.setPaymentStatus(StoreOrderStatus.PARTIALLY_REFUNDED);
            }
            payment.setUpdatedAt(Instant.now());
            order.setUpdatedAt(Instant.now());
            transitionOrderStatus(order, StoreOrderStatus.CANCELLED, "İade yapıldı: " + request.getAmount() + " TRY");
        }

        return mapper.toOrderDetailDTO(order);
    }

    public StoreBankTransfer createBankTransfer(StoreBankTransferRequest request) {
        Profile profile = appSupport.currentProfile();
        StoreOrder order = orderRepository
            .findByOrderNumber(request.getOrderNumber())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!order.getUser().getId().equals(profile.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu siparişe erişim yetkiniz yok");
        }
        if (order.getOrderStatus() != StoreOrderStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sipariş için ödeme beklenmiyor");
        }

        bankTransferRepository
            .findByOrderId(order.getId())
            .ifPresent(bt -> {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu sipariş için havale kaydı zaten var");
            });

        StoreBankTransfer transfer = new StoreBankTransfer();
        transfer.setOrder(order);
        transfer.setReferenceCode(generateBankTransferReference());
        transfer.setAmount(order.getTotalAmount());
        transfer.setCurrency(order.getCurrency());
        transfer.setIban("TR00 1234 0000 0000 0000 0000 00");
        transfer.setAccountHolder("Bialem Teknoloji A.Ş.");
        transfer.setBankName("Örnek Bank");
        transfer.setReceiptUrl(request.getReceiptUrl());
        transfer.setStatus(StorePaymentStatus.PENDING);
        transfer.setCreatedAt(Instant.now());
        bankTransferRepository.save(transfer);

        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme kaydı bulunamadı"));
        payment.setPaymentMethod(StorePaymentMethod.BANK_TRANSFER);
        payment.setStatus(StorePaymentStatus.PENDING);
        payment.setUpdatedAt(Instant.now());
        paymentRepository.save(payment);

        addOrderStatusHistory(order, StoreOrderStatus.PENDING_PAYMENT, "Havale/EFT bekleniyor: " + transfer.getReferenceCode());
        return transfer;
    }

    public StoreBankTransfer approveBankTransfer(Long transferId, String adminNote) {
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN", "ROLE_STORE_MANAGER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkiniz yok");
        }
        Profile admin = appSupport.currentProfile();
        StoreBankTransfer transfer = bankTransferRepository
            .findById(transferId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Havale kaydı bulunamadı"));
        StoreOrder order = transfer.getOrder();
        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme kaydı bulunamadı"));

        transfer.setStatus(StorePaymentStatus.SUCCESS);
        transfer.setAdminNote(adminNote);
        transfer.setApprovedBy(admin);
        transfer.setApprovedAt(Instant.now());
        transfer.setUpdatedAt(Instant.now());

        processPaymentSuccess(payment, order, transfer.getReferenceCode());
        return transfer;
    }

    public StoreBankTransfer rejectBankTransfer(Long transferId, String adminNote) {
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN", "ROLE_STORE_MANAGER")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yetkiniz yok");
        }
        Profile admin = appSupport.currentProfile();
        StoreBankTransfer transfer = bankTransferRepository
            .findById(transferId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Havale kaydı bulunamadı"));
        StoreOrder order = transfer.getOrder();
        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme kaydı bulunamadı"));

        transfer.setStatus(StorePaymentStatus.FAILED);
        transfer.setAdminNote(adminNote);
        transfer.setApprovedBy(admin);
        transfer.setApprovedAt(Instant.now());
        transfer.setUpdatedAt(Instant.now());

        processPaymentFailure(payment, order, "Havale/EFT reddedildi");
        return transfer;
    }

    private void processPaymentSuccess(StorePayment payment, StoreOrder order, String providerReference) {
        if (payment.getStatus() == StorePaymentStatus.SUCCESS) {
            LOG.warn("Payment already successful for order {}", order.getOrderNumber());
            return;
        }
        payment.setStatus(StorePaymentStatus.SUCCESS);
        payment.setTransactionId(providerReference);
        payment.setPaidAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        order.setPaymentStatus(StoreOrderStatus.PAID);

        transitionOrderStatus(order, StoreOrderStatus.PAID, "Ödeme alındı");
        for (StoreOrderItem item : order.getItems()) {
            StoreProduct product = productRepository.findById(item.getProductId()).orElse(null);
            if (product == null) continue;
            int qty = item.getQuantity();
            product.setStockQuantity(Math.max(0, product.getStockQuantity() - qty));
            product.setSalesCount((product.getSalesCount() == null ? 0 : product.getSalesCount()) + qty);
            product.setUpdatedAt(Instant.now());
        }
        transitionOrderStatus(order, StoreOrderStatus.WAITING_ADMIN_APPROVAL, "Sipariş onay bekliyor");
        cartItemRepository.deleteByUserId(order.getUser().getId());
        publishEvent(NotificationEventType.STORE_ORDER_CREATED, order, payment);
        publishEvent(NotificationEventType.STORE_PAYMENT_SUCCESS, order, payment);
    }

    private void processPaymentFailure(StorePayment payment, StoreOrder order, String reason) {
        payment.setStatus(StorePaymentStatus.FAILED);
        payment.setFailureReason(reason);
        payment.setUpdatedAt(Instant.now());
        order.setPaymentStatus(StoreOrderStatus.PAYMENT_FAILED);
        transitionOrderStatus(order, StoreOrderStatus.PAYMENT_FAILED, "Ödeme başarısız: " + reason);
        publishEvent(NotificationEventType.STORE_PAYMENT_FAILED, order, null);
    }

    private void transitionOrderStatus(StoreOrder order, StoreOrderStatus newStatus, String note) {
        StoreOrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        addOrderStatusHistory(order, oldStatus, newStatus, note);
    }

    private void addOrderStatusHistory(StoreOrder order, StoreOrderStatus newStatus, String note) {
        addOrderStatusHistory(order, order.getOrderStatus(), newStatus, note);
    }

    private void addOrderStatusHistory(StoreOrder order, StoreOrderStatus oldStatus, StoreOrderStatus newStatus, String note) {
        StoreOrderStatusHistory history = new StoreOrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNote(note);
        history.setCreatedAt(Instant.now());
        statusHistoryRepository.save(history);
    }

    private void publishEvent(NotificationEventType type, StoreOrder order, StorePayment payment) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", order.getId());
            variables.put("orderNumber", order.getOrderNumber());
            if (payment != null) {
                variables.put("paymentId", payment.getId());
            }
            eventPublisher.publish(new NotificationEvent(type, order.getOrderNumber(), variables));
        } catch (Exception e) {
            LOG.warn("Failed to publish notification event", e);
        }
    }

    private StorePaymentMethod parsePaymentMethod(String value) {
        try {
            return StorePaymentMethod.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz ödeme yöntemi");
        }
    }

    private StorePaymentStatus parseStatus(String status) {
        if (status == null) return StorePaymentStatus.PROCESSING;
        try {
            return StorePaymentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return StorePaymentStatus.PROCESSING;
        }
    }

    private String maskSensitiveData(StorePaymentInitiateRequest request) {
        String card = request.getCardNumber() != null ? request.getCardNumber().replaceAll("(?<=.{4}).(?=.{4})", "*") : null;
        return "cardHolder=" +
        request.getCardHolderName() +
        ", cardLastFour=" +
        card +
        ", method=" +
        request.getPaymentMethod();
    }

    private String generateBankTransferReference() {
        return "BL-" + java.time.Year.now().getValue() + "-" + String.format("%06d", (int) (Math.random() * 1_000_000));
    }
}
