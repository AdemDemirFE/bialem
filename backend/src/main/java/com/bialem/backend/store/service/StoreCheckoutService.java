package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.User;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.domain.*;
import com.bialem.backend.store.domain.enumeration.*;
import com.bialem.backend.store.repository.*;
import com.bialem.backend.store.service.dto.*;
import com.bialem.backend.store.service.mapper.StoreMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreCheckoutService {

    private static final Logger LOG = LoggerFactory.getLogger(StoreCheckoutService.class);

    private final StoreCartItemRepository cartItemRepository;
    private final StoreAddressRepository addressRepository;
    private final StoreProductRepository productRepository;
    private final StoreProductVariantRepository variantRepository;
    private final StoreOrderRepository orderRepository;
    private final StoreOrderItemRepository orderItemRepository;
    private final StoreOrderStatusHistoryRepository orderStatusHistoryRepository;
    private final StorePaymentRepository paymentRepository;
    private final StoreCouponRepository couponRepository;
    private final StoreMapper mapper;
    private final ObjectMapper objectMapper;
    private final AppSupport appSupport;
    private final NotificationEventPublisher eventPublisher;

    public StoreCheckoutService(
        StoreCartItemRepository cartItemRepository,
        StoreAddressRepository addressRepository,
        StoreProductRepository productRepository,
        StoreProductVariantRepository variantRepository,
        StoreOrderRepository orderRepository,
        StoreOrderItemRepository orderItemRepository,
        StoreOrderStatusHistoryRepository orderStatusHistoryRepository,
        StorePaymentRepository paymentRepository,
        StoreCouponRepository couponRepository,
        StoreMapper mapper,
        ObjectMapper objectMapper,
        AppSupport appSupport,
        NotificationEventPublisher eventPublisher
    ) {
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.paymentRepository = paymentRepository;
        this.couponRepository = couponRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.appSupport = appSupport;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public StoreCartSummaryDTO getCheckoutSummary(Profile user) {
        List<StoreCartItem> items = cartItemRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return mapper.toCartSummary(items);
    }

    public StoreOrderDetailDTO checkout(Profile user, StoreCheckoutRequest request) {
        if (paymentRepository.findByIdempotencyKey(request.getIdempotencyKey()).isPresent()) {
            LOG.warn("Duplicate checkout idempotency key: {}", request.getIdempotencyKey());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Bu ödeme daha önce işlendi");
        }
        StoreAddress shippingAddress = addressRepository
            .findByIdAndUserId(request.getShippingAddressId(), user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Teslimat adresi bulunamadı"));
        StoreAddress billingAddress = request.getBillingAddressId() != null
            ? addressRepository.findByIdAndUserId(request.getBillingAddressId(), user.getId()).orElse(shippingAddress)
            : shippingAddress;

        List<StoreCartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sepetiniz boş");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<StoreOrderItem> orderItems = new ArrayList<>();
        for (StoreCartItem cartItem : cartItems) {
            StoreProduct product = cartItem.getProduct();
            StoreProductVariant variant = cartItem.getVariant();
            int available = mapper.availableStock(product, variant);
            if (available < cartItem.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, product.getName() + " için yeterli stok yok");
            }
            if (!StoreProductStatus.ACTIVE.equals(product.getStatus()) || Boolean.FALSE.equals(product.getIsActive())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, product.getName() + " satışa açık değil");
            }
            BigDecimal unitPrice = mapper.effectivePrice(product, variant);
            BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(total);

            StoreOrderItem orderItem = new StoreOrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductNameSnapshot(product.getName());
            orderItem.setProductSkuSnapshot(product.getSku());
            orderItem.setProductImageSnapshot(mapper.toProductDTO(product).getImages().isEmpty() ? null : mapper.toProductDTO(product).getImages().get(0).getImageUrl());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setDiscount(BigDecimal.ZERO);
            orderItem.setTotalPrice(total);
            orderItem.setVariantSnapshot(variant != null ? variant.getVariantName() : null);
            orderItem.setCreatedAt(Instant.now());
            orderItems.add(orderItem);
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            discountAmount = calculateCouponDiscount(request.getCouponCode(), subtotal, user.getId());
        }
        BigDecimal shippingAmount = BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.add(shippingAmount).subtract(discountAmount);

        StorePaymentProviderType providerType = mapper.fromPaymentProvider(request.getPaymentProvider());
        if (providerType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz ödeme sağlayıcısı");
        }

        StoreOrder order = new StoreOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setShippingAddressSnapshot(mapper.toAddressSnapshot(mapper.toAddressDTO(shippingAddress)));
        order.setBillingAddressSnapshot(mapper.toAddressSnapshot(mapper.toAddressDTO(billingAddress)));
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setShippingAmount(shippingAmount);
        order.setTotalAmount(totalAmount);
        order.setCurrency("TRY");
        order.setPaymentStatus(StoreOrderStatus.PENDING_PAYMENT);
        order.setOrderStatus(StoreOrderStatus.PENDING_PAYMENT);
        order.setShippingStatus(StoreOrderStatus.PENDING_PAYMENT);
        order.setCustomerNote(request.getCustomerNote());
        order.setCouponCode(request.getCouponCode());
        order.setCreatedAt(Instant.now());

        orderItems.forEach(item -> item.setOrder(order));
        order.setItems(orderItems);
        orderRepository.save(order);

        StorePayment payment = new StorePayment();
        payment.setOrder(order);
        payment.setProvider(providerType);
        payment.setAmount(totalAmount);
        payment.setCurrency("TRY");
        payment.setStatus(StorePaymentStatus.PENDING);
        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setCreatedAt(Instant.now());
        paymentRepository.save(payment);

        addStatusHistory(order, null, StoreOrderStatus.PENDING_PAYMENT, "Sipariş oluşturuldu, ödeme bekleniyor");

        return mapper.toOrderDetailDTO(order);
    }

    public StoreOrderDetailDTO handlePaymentCallback(StorePaymentCallbackRequest request) {
        StoreOrder order = orderRepository
            .findByOrderNumber(request.getOrderNumber())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        StorePayment payment = paymentRepository
            .findByOrderId(order.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ödeme bulunamadı"));
        if (payment.getTransactionId() != null && payment.getTransactionId().equals(request.getProviderTransactionId()) && payment.getStatus() == StorePaymentStatus.SUCCESS) {
            LOG.warn("Duplicate payment callback ignored for order {}", request.getOrderNumber());
            return mapper.toOrderDetailDTO(order);
        }
        payment.setTransactionId(request.getProviderTransactionId());
        payment.setCallbackPayload(request.getPayload());
        payment.setUpdatedAt(Instant.now());
        if ("SUCCESS".equalsIgnoreCase(request.getStatus())) {
            processPayment(payment, order);
            fulfillOrder(order, payment);
        } else {
            payment.setStatus(StorePaymentStatus.FAILED);
            payment.setFailureReason(request.getStatus());
            transitionOrderStatus(order, StoreOrderStatus.PAYMENT_FAILED, "Ödeme başarısız: " + request.getStatus());
            publishEvent(NotificationEventType.STORE_PAYMENT_FAILED, order, null);
        }
        return mapper.toOrderDetailDTO(order);
    }

    private boolean processPayment(StorePayment payment, StoreOrder order) {
        if (payment.getStatus() == StorePaymentStatus.SUCCESS) {
            return true;
        }
        payment.setStatus(StorePaymentStatus.SUCCESS);
        payment.setPaidAt(Instant.now());
        payment.setTransactionId(payment.getTransactionId() == null ? UUID.randomUUID().toString() : payment.getTransactionId());
        payment.setUpdatedAt(Instant.now());
        return true;
    }

    private void fulfillOrder(StoreOrder order, StorePayment payment) {
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

    private void transitionOrderStatus(StoreOrder order, StoreOrderStatus newStatus, String note) {
        StoreOrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);
        order.setPaymentStatus(newStatus);
        order.setShippingStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        addStatusHistory(order, oldStatus, newStatus, note);
    }

    private void addStatusHistory(StoreOrder order, StoreOrderStatus oldStatus, StoreOrderStatus newStatus, String note) {
        StoreOrderStatusHistory history = new StoreOrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNote(note);
        history.setCreatedAt(Instant.now());
        order.getStatusHistory().add(history);
    }

    private BigDecimal calculateCouponDiscount(String code, BigDecimal subtotal, Long userId) {
        StoreCoupon coupon = couponRepository
            .findByCodeAndIsActiveTrue(code)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon geçersiz"));
        Instant now = Instant.now();
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon henüz aktif değil");
        }
        if (coupon.getEndDate() != null && now.isAfter(coupon.getEndDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon süresi dolmuş");
        }
        if (coupon.getMinimumCartAmount() != null && subtotal.compareTo(coupon.getMinimumCartAmount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum sepet tutarı karşılanmadı");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon kullanım limiti doldu");
        }
        BigDecimal discount;
        if (coupon.getDiscountType() == StoreCouponDiscountType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getDiscountValue();
        }
        if (coupon.getMaximumDiscount() != null && discount.compareTo(coupon.getMaximumDiscount()) > 0) {
            discount = coupon.getMaximumDiscount();
        }
        coupon.setUsageCount(coupon.getUsageCount() + 1);
        return discount;
    }

    private String generateOrderNumber() {
        return "ORD-" + DateTimeFormatter.ofPattern("yyyy-MMdd").format(java.time.LocalDate.now()) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void publishEvent(NotificationEventType type, StoreOrder order, StorePayment payment) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientUserId", order.getUser().getId());
            variables.put("orderId", order.getId());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("totalAmount", order.getTotalAmount());
            variables.put("currency", order.getCurrency());
            variables.put("customerName", order.getUser().getDisplayName());
            if (payment != null) {
                variables.put("paymentProvider", payment.getProvider().name());
                variables.put("paymentStatus", payment.getStatus().name());
            }
            eventPublisher.publish(new NotificationEvent(type, order.getOrderNumber() + ":" + type.name(), variables));
        } catch (Exception ex) {
            LOG.warn("Failed to publish store notification event {}", type, ex);
        }
    }
}
