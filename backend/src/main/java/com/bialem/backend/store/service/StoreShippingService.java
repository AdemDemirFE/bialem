package com.bialem.backend.store.service;

import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.StoreShipping;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import com.bialem.backend.store.domain.enumeration.StoreShippingStatus;
import com.bialem.backend.store.repository.StoreOrderRepository;
import com.bialem.backend.store.repository.StoreShippingRepository;
import com.bialem.backend.store.service.dto.StoreOrderDetailDTO;
import com.bialem.backend.store.service.dto.StoreShippingDTO;
import com.bialem.backend.store.service.dto.StoreShippingRequest;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreShippingService {

    private static final Logger LOG = LoggerFactory.getLogger(StoreShippingService.class);

    private final StoreShippingRepository shippingRepository;
    private final StoreOrderRepository orderRepository;
    private final StoreMapper mapper;
    private final NotificationEventPublisher eventPublisher;

    public StoreShippingService(
        StoreShippingRepository shippingRepository,
        StoreOrderRepository orderRepository,
        StoreMapper mapper,
        NotificationEventPublisher eventPublisher
    ) {
        this.shippingRepository = shippingRepository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreOrderDetailDTO createShipping(Long orderId, StoreShippingRequest request, String changedBy) {
        StoreOrder order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        if (order.getOrderStatus() != StoreOrderStatus.READY_FOR_SHIPPING && order.getOrderStatus() != StoreOrderStatus.PREPARING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sipariş kargoya verilmeye hazır değil");
        }
        StoreShipping shipping = new StoreShipping();
        shipping.setOrder(order);
        shipping.setCarrier(request.getCarrier());
        shipping.setTrackingNumber(request.getTrackingNumber());
        shipping.setShippingStatus(StoreShippingStatus.SHIPPED);
        shipping.setShippedAt(Instant.now());
        shipping.setEstimatedDeliveryDate(request.getEstimatedDeliveryDate());
        shipping.setCreatedAt(Instant.now());
        shippingRepository.save(shipping);
        order.setShipping(shipping);
        order.setOrderStatus(StoreOrderStatus.SHIPPED);
        order.setShippingStatus(StoreOrderStatus.SHIPPED);
        order.setUpdatedAt(Instant.now());
        publishShippingEvent(NotificationEventType.STORE_ORDER_SHIPPED, order);
        return mapper.toOrderDetailDTO(order);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreOrderDetailDTO updateShippingStatus(Long orderId, StoreShippingStatus status, String changedBy) {
        StoreOrder order = orderRepository.findById(orderId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        StoreShipping shipping = shippingRepository
            .findByOrderId(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kargo bilgisi bulunamadı"));
        shipping.setShippingStatus(status);
        shipping.setUpdatedAt(Instant.now());
        if (status == StoreShippingStatus.DELIVERED) {
            shipping.setDeliveredAt(Instant.now());
            order.setOrderStatus(StoreOrderStatus.DELIVERED);
            order.setShippingStatus(StoreOrderStatus.DELIVERED);
            publishShippingEvent(NotificationEventType.STORE_ORDER_DELIVERED, order);
        } else if (status == StoreShippingStatus.IN_TRANSIT) {
            order.setOrderStatus(StoreOrderStatus.IN_TRANSIT);
            order.setShippingStatus(StoreOrderStatus.IN_TRANSIT);
            publishShippingEvent(NotificationEventType.STORE_ORDER_IN_TRANSIT, order);
        } else if (status == StoreShippingStatus.OUT_FOR_DELIVERY) {
            order.setOrderStatus(StoreOrderStatus.OUT_FOR_DELIVERY);
            order.setShippingStatus(StoreOrderStatus.OUT_FOR_DELIVERY);
            publishShippingEvent(NotificationEventType.STORE_ORDER_OUT_FOR_DELIVERY, order);
        }
        order.setUpdatedAt(Instant.now());
        return mapper.toOrderDetailDTO(order);
    }

    @Transactional(readOnly = true)
    public StoreShippingDTO getShippingByOrderId(Long orderId) {
        return shippingRepository
            .findByOrderId(orderId)
            .map(mapper::toShippingDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kargo bilgisi bulunamadı"));
    }

    private void publishShippingEvent(NotificationEventType type, StoreOrder order) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("recipientUserId", order.getUser().getId());
            variables.put("orderId", order.getId());
            variables.put("orderNumber", order.getOrderNumber());
            if (order.getShipping() != null) {
                variables.put("trackingNumber", order.getShipping().getTrackingNumber());
                variables.put("carrier", order.getShipping().getCarrier());
            }
            eventPublisher.publish(new NotificationEvent(type, order.getOrderNumber() + ":" + type.name(), variables));
        } catch (Exception ex) {
            LOG.warn("Failed to publish shipping event {}", type, ex);
        }
    }
}
