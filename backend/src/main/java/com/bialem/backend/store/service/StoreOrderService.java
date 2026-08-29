package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.StoreOrderStatusHistory;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import com.bialem.backend.store.repository.StoreOrderRepository;
import com.bialem.backend.store.repository.StoreOrderStatusHistoryRepository;
import com.bialem.backend.store.service.dto.StoreOrderDTO;
import com.bialem.backend.store.service.dto.StoreOrderDetailDTO;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(StoreOrderService.class);

    private final StoreOrderRepository orderRepository;
    private final StoreOrderStatusHistoryRepository orderStatusHistoryRepository;
    private final StoreMapper mapper;
    private final NotificationEventPublisher eventPublisher;

    public StoreOrderService(
        StoreOrderRepository orderRepository,
        StoreOrderStatusHistoryRepository orderStatusHistoryRepository,
        StoreMapper mapper,
        NotificationEventPublisher eventPublisher
    ) {
        this.orderRepository = orderRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public Page<StoreOrderDTO> getUserOrders(Profile user, StoreOrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable).map(mapper::toOrderDTO);
        }
        return orderRepository.findByUserIdAndOrderStatusOrderByCreatedAtDesc(user.getId(), status, pageable).map(mapper::toOrderDTO);
    }

    @Transactional(readOnly = true)
    public StoreOrderDetailDTO getUserOrderDetail(Profile user, Long id) {
        StoreOrder order = orderRepository
            .findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        return mapper.toOrderDetailDTO(order);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @Transactional(readOnly = true)
    public Page<StoreOrderDTO> getAdminOrders(StoreOrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.findAll(pageable).map(mapper::toOrderDTO);
        }
        return orderRepository.findByOrderStatusOrderByCreatedAtDesc(status, pageable).map(mapper::toOrderDTO);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @Transactional(readOnly = true)
    public StoreOrderDetailDTO getAdminOrderDetail(Long id) {
        StoreOrder order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        return mapper.toOrderDetailDTO(order);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreOrderDetailDTO approveOrder(Long id, String changedBy) {
        StoreOrder order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!canTransition(order.getOrderStatus(), StoreOrderStatus.APPROVED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu durum geçişine izin verilmiyor");
        }
        transition(order, StoreOrderStatus.APPROVED, changedBy, "Sipariş onaylandı");
        publishOrderEvent(NotificationEventType.STORE_ORDER_APPROVED, order);
        return mapper.toOrderDetailDTO(order);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreOrderDetailDTO markPreparing(Long id, String changedBy) {
        StoreOrder order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!canTransition(order.getOrderStatus(), StoreOrderStatus.PREPARING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu durum geçişine izin verilmiyor");
        }
        transition(order, StoreOrderStatus.PREPARING, changedBy, "Sipariş hazırlanıyor");
        publishOrderEvent(NotificationEventType.STORE_ORDER_PREPARING, order);
        return mapper.toOrderDetailDTO(order);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreOrderDetailDTO markReadyForShipping(Long id, String changedBy) {
        StoreOrder order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (!canTransition(order.getOrderStatus(), StoreOrderStatus.READY_FOR_SHIPPING)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu durum geçişine izin verilmiyor");
        }
        transition(order, StoreOrderStatus.READY_FOR_SHIPPING, changedBy, "Sipariş kargoya hazır");
        return mapper.toOrderDetailDTO(order);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreOrderDetailDTO cancelOrder(Long id, String reason, String changedBy) {
        StoreOrder order = orderRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (order.getOrderStatus() == StoreOrderStatus.DELIVERED || order.getOrderStatus() == StoreOrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu sipariş iptal edilemez");
        }
        transition(order, StoreOrderStatus.CANCELLED, changedBy, "Sipariş iptal edildi: " + reason);
        publishOrderEvent(NotificationEventType.STORE_ORDER_CANCELLED, order);
        return mapper.toOrderDetailDTO(order);
    }

    public StoreOrderDetailDTO cancelOwnOrder(Profile user, Long id, String reason) {
        StoreOrder order = orderRepository
            .findByIdAndUserId(id, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (order.getOrderStatus() == StoreOrderStatus.DELIVERED || order.getOrderStatus() == StoreOrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu sipariş iptal edilemez");
        }
        transition(order, StoreOrderStatus.CANCELLED, user.getDisplayName(), "Müşteri tarafından iptal edildi: " + reason);
        publishOrderEvent(NotificationEventType.STORE_ORDER_CANCELLED, order);
        return mapper.toOrderDetailDTO(order);
    }

    private void transition(StoreOrder order, StoreOrderStatus newStatus, String changedBy, String note) {
        StoreOrderStatus oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus);
        order.setPaymentStatus(newStatus);
        order.setShippingStatus(newStatus);
        order.setUpdatedAt(Instant.now());
        StoreOrderStatusHistory history = new StoreOrderStatusHistory();
        history.setOrder(order);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setNote(note);
        history.setCreatedAt(Instant.now());
        order.getStatusHistory().add(history);
    }

    private boolean canTransition(StoreOrderStatus from, StoreOrderStatus to) {
        return switch (from) {
            case PENDING_PAYMENT, PAID -> to == StoreOrderStatus.APPROVED || to == StoreOrderStatus.CANCELLED || to == StoreOrderStatus.PAYMENT_FAILED;
            case WAITING_ADMIN_APPROVAL -> to == StoreOrderStatus.APPROVED || to == StoreOrderStatus.CANCELLED;
            case APPROVED -> to == StoreOrderStatus.PREPARING || to == StoreOrderStatus.CANCELLED;
            case PREPARING -> to == StoreOrderStatus.READY_FOR_SHIPPING || to == StoreOrderStatus.CANCELLED;
            case READY_FOR_SHIPPING -> to == StoreOrderStatus.SHIPPED || to == StoreOrderStatus.CANCELLED;
            case SHIPPED -> to == StoreOrderStatus.IN_TRANSIT;
            case IN_TRANSIT -> to == StoreOrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> to == StoreOrderStatus.DELIVERED;
            default -> false;
        };
    }

    private void publishOrderEvent(NotificationEventType type, StoreOrder order) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientUserId", order.getUser().getId());
            variables.put("orderId", order.getId());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("totalAmount", order.getTotalAmount());
            variables.put("currency", order.getCurrency());
            variables.put("customerName", order.getUser().getDisplayName());
            eventPublisher.publish(new NotificationEvent(type, order.getOrderNumber() + ":" + type.name(), variables));
        } catch (Exception ex) {
            LOG.warn("Failed to publish order event {}", type, ex);
        }
    }
}
