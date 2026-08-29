package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.service.StoreCheckoutService;
import com.bialem.backend.store.service.dto.StoreCartSummaryDTO;
import com.bialem.backend.store.service.dto.StoreCheckoutRequest;
import com.bialem.backend.store.service.dto.StoreOrderDetailDTO;
import com.bialem.backend.store.service.dto.StorePaymentCallbackRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/checkout")
public class StoreCheckoutResource {

    private final StoreCheckoutService checkoutService;
    private final AppSupport appSupport;

    public StoreCheckoutResource(StoreCheckoutService checkoutService, AppSupport appSupport) {
        this.checkoutService = checkoutService;
        this.appSupport = appSupport;
    }

    @GetMapping("/summary")
    public ResponseEntity<StoreCartSummaryDTO> getSummary() {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(checkoutService.getCheckoutSummary(profile));
    }

    @PostMapping
    public ResponseEntity<StoreOrderDetailDTO> checkout(@Valid @RequestBody StoreCheckoutRequest request) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(checkoutService.checkout(profile, request));
    }

    @PostMapping("/payment-callback")
    public ResponseEntity<StoreOrderDetailDTO> paymentCallback(@Valid @RequestBody StorePaymentCallbackRequest request) {
        return ResponseEntity.ok(checkoutService.handlePaymentCallback(request));
    }
}
