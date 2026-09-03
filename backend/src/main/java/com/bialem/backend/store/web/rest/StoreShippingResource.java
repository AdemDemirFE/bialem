package com.bialem.backend.store.web.rest;

import com.bialem.backend.store.domain.enumeration.StoreShippingStatus;
import com.bialem.backend.store.service.StoreShippingService;
import com.bialem.backend.store.service.dto.StoreOrderDetailDTO;
import com.bialem.backend.store.service.dto.StoreShippingDTO;
import com.bialem.backend.store.service.dto.StoreShippingRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/orders/{orderId}")
public class StoreShippingResource {

    private final StoreShippingService shippingService;

    public StoreShippingResource(StoreShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @GetMapping("/shipping")
    public ResponseEntity<StoreShippingDTO> getShipping(@PathVariable Long orderId) {
        return ResponseEntity.ok(shippingService.getShippingByOrderId(orderId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/ship")
    public ResponseEntity<StoreOrderDetailDTO> createShipping(@PathVariable Long orderId, @Valid @RequestBody StoreShippingRequest request) {
        return ResponseEntity.ok(shippingService.createShipping(orderId, request, "admin"));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/shipping-status")
    public ResponseEntity<StoreOrderDetailDTO> updateShippingStatus(@PathVariable Long orderId, @RequestParam StoreShippingStatus status) {
        return ResponseEntity.ok(shippingService.updateShippingStatus(orderId, status, "admin"));
    }
}
