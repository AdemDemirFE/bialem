package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import com.bialem.backend.store.service.StoreOrderService;
import com.bialem.backend.store.service.dto.StoreOrderDTO;
import com.bialem.backend.store.service.dto.StoreOrderDetailDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/orders")
public class StoreOrderResource {

    private final StoreOrderService orderService;
    private final AppSupport appSupport;

    public StoreOrderResource(StoreOrderService orderService, AppSupport appSupport) {
        this.orderService = orderService;
        this.appSupport = appSupport;
    }

    @GetMapping
    public ResponseEntity<Page<StoreOrderDTO>> getMyOrders(@RequestParam(required = false) StoreOrderStatus status, Pageable pageable) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.getUserOrders(profile, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreOrderDetailDTO> getMyOrder(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.getUserOrderDetail(profile, id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<StoreOrderDetailDTO> cancelMyOrder(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.cancelOwnOrder(profile, id, reason));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<Page<StoreOrderDTO>> getAdminOrders(@RequestParam(required = false) StoreOrderStatus status, Pageable pageable) {
        return ResponseEntity.ok(orderService.getAdminOrders(status, pageable));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @GetMapping("/admin/{id}")
    public ResponseEntity<StoreOrderDetailDTO> getAdminOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getAdminOrderDetail(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/{id}/approve")
    public ResponseEntity<StoreOrderDetailDTO> approveOrder(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.approveOrder(id, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/{id}/preparing")
    public ResponseEntity<StoreOrderDetailDTO> markPreparing(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.markPreparing(id, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/{id}/ready-for-shipping")
    public ResponseEntity<StoreOrderDetailDTO> markReadyForShipping(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.markReadyForShipping(id, profile.getDisplayName()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/admin/{id}/cancel")
    public ResponseEntity<StoreOrderDetailDTO> cancelOrder(@PathVariable Long id, @RequestParam(required = false) String reason) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(orderService.cancelOrder(id, reason, profile.getDisplayName()));
    }
}
