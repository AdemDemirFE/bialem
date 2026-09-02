package com.bialem.backend.payment;

import com.bialem.backend.domain.*;
import com.bialem.backend.domain.enumeration.*;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates ticket orders, payments and ticket issuance.
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final EventTicketRepository eventTicketRepository;
    private final NotificationEventPublisher notificationEvents;
    private final List<PaymentProvider> providers;

    public PaymentService(
        OrderRepository orderRepository,
        OrderItemRepository orderItemRepository,
        PaymentRepository paymentRepository,
        TicketRepository ticketRepository,
        EventTicketRepository eventTicketRepository,
        NotificationEventPublisher notificationEvents,
        List<PaymentProvider> providers
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.eventTicketRepository = eventTicketRepository;
        this.notificationEvents = notificationEvents;
        this.providers = providers;
    }

    public record TicketRequest(Long ticketId, Integer quantity) {}

    /**
     * Create a pending order with stock reservation.
     */
    public Order createOrder(Profile user, List<TicketRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Siparişte en az bir bilet seçilmelidir.");
        }
        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setCurrency("TRY");
        order.setCreatedAt(Instant.now());
        order.setTotalAmount(BigDecimal.ZERO);
        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        for (TicketRequest request : items) {
            EventTicket ticket = eventTicketRepository.findById(request.ticketId())
                .orElseThrow(() -> new IllegalArgumentException("Bilet tipi bulunamadı: " + request.ticketId()));
            if (ticket.getStatus() != EventTicketStatus.ACTIVE) {
                throw new IllegalArgumentException("Bu bilet tipi satışta değil: " + ticket.getName());
            }
            if (request.quantity() == null || request.quantity() <= 0) {
                throw new IllegalArgumentException("Geçersiz bilet adedi.");
            }
            int available = ticket.getQuantity() - (ticket.getSoldQuantity() == null ? 0 : ticket.getSoldQuantity());
            if (available < request.quantity()) {
                throw new IllegalArgumentException("Yeterli kontenjan yok: " + ticket.getName());
            }
            ticket.setSoldQuantity((ticket.getSoldQuantity() == null ? 0 : ticket.getSoldQuantity()) + request.quantity());
            eventTicketRepository.save(ticket);

            BigDecimal unitPrice = ticket.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(request.quantity()));
            total = total.add(lineTotal);

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setTicket(ticket);
            item.setQuantity(request.quantity());
            item.setUnitPrice(unitPrice);
            item.setTotalPrice(lineTotal);
            orderItemRepository.save(item);
        }
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    /**
     * Start payment for an order and return provider checkout data.
     */
    public PaymentInitiationResult initiatePayment(Order order, PaymentProviderType providerType, String idempotencyKey, String callbackUrl) {
        PaymentProvider provider = providers.stream()
            .filter(p -> p.getType() == providerType)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ödeme sağlayıcısı bulunamadı: " + providerType));

        Optional<Payment> existing = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            LOG.info("Re-using existing payment for idempotency key {}", idempotencyKey);
            Payment payment = existing.orElseThrow();
            return PaymentInitiationResult.success(
                payment.getProviderTransactionId(),
                callbackUrl + "?transactionId=" + payment.getProviderTransactionId(),
                payment.getProviderTransactionId(),
                Map.of("provider", providerType.name().toLowerCase())
            );
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setProvider(providerType);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(order.getCurrency());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setIdempotencyKey(idempotencyKey);
        payment = paymentRepository.save(payment);

        PaymentInitiationResult result = provider.initiatePayment(payment, order, callbackUrl);
        if (result.success()) {
            payment.setProviderTransactionId(result.providerTransactionId());
            paymentRepository.save(payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(result.errorMessage());
            paymentRepository.save(payment);
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
        }
        return result;
    }

    /**
     * Handle provider callback idempotently. On success create tickets and notify the user.
     */
    public Payment handleCallback(String providerTransactionId, String payload, PaymentProviderType providerType) {
        PaymentProvider provider = providers.stream()
            .filter(p -> p.getType() == providerType)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Ödeme sağlayıcısı bulunamadı: " + providerType));

        Payment payment = paymentRepository.findByProviderTransactionId(providerTransactionId)
            .orElseThrow(() -> new IllegalArgumentException("Ödeme kaydı bulunamadı: " + providerTransactionId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            LOG.info("Payment {} already completed; skipping duplicate callback.", providerTransactionId);
            return payment;
        }
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            LOG.info("Payment {} already refunded; ignoring callback.", providerTransactionId);
            return payment;
        }

        payment.setCallbackPayload(payload);
        PaymentVerificationResult verification = provider.verifyCallback(providerTransactionId, payload);
        if (verification.verified()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(Instant.now());
            payment = paymentRepository.save(payment);

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.PAID);
            order.setPaidAt(Instant.now());
            order = orderRepository.save(order);

            issueTickets(order);
            sendPaymentSuccessNotification(order);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(verification.failureReason());
            payment = paymentRepository.save(payment);

            Order order = payment.getOrder();
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
        }
        return payment;
    }

    private void issueTickets(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
        for (OrderItem item : items) {
            EventTicket eventTicket = item.getTicket();
            Event event = eventTicket.getEvent();
            for (int i = 0; i < item.getQuantity(); i++) {
                Ticket ticket = new Ticket();
                ticket.setOrderItem(item);
                ticket.setUser(order.getUser());
                ticket.setEvent(event);
                ticket.setTicketCode("BIA-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
                ticket.setQrCode("bialem://ticket/" + ticket.getTicketCode());
                ticket.setStatus(TicketStatus.ACTIVE);
                ticketRepository.save(ticket);
            }
        }
    }

    private void sendPaymentSuccessNotification(Order order) {
        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("recipientUserId", order.getUser().getUser().getId());
        variables.put("actorUserId", order.getUser().getUser().getId());
        variables.put("actorProfileId", order.getUser().getId());
        variables.put("actorName", order.getUser().getDisplayName());
        variables.put("referenceType", "ORDER");
        variables.put("referenceId", order.getId());
        variables.put("route", "/tickets");
        notificationEvents.publish(new NotificationEvent(NotificationEventType.TICKET_PURCHASED,
            "ticket-purchase:" + order.getId(), variables));
    }

    /**
     * Cancel an order and restore ticket stock.
     */
    public void cancelOrder(Order order) {
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Ödenmiş sipariş iptal edilemez; iade işlemi yapılmalıdır.");
        }
        List<OrderItem> items = orderItemRepository.findByOrder_Id(order.getId());
        for (OrderItem item : items) {
            EventTicket ticket = item.getTicket();
            ticket.setSoldQuantity((ticket.getSoldQuantity() == null ? 0 : ticket.getSoldQuantity()) - item.getQuantity());
            eventTicketRepository.save(ticket);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}
