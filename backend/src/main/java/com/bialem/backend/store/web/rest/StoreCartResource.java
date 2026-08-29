package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.service.StoreCartService;
import com.bialem.backend.store.service.dto.StoreCartItemRequest;
import com.bialem.backend.store.service.dto.StoreCartSummaryDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/cart")
public class StoreCartResource {

    private final StoreCartService cartService;
    private final AppSupport appSupport;

    public StoreCartResource(StoreCartService cartService, AppSupport appSupport) {
        this.cartService = cartService;
        this.appSupport = appSupport;
    }

    @GetMapping
    public ResponseEntity<StoreCartSummaryDTO> getCart() {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(cartService.getCart(profile));
    }

    @PostMapping("/items")
    public ResponseEntity<StoreCartSummaryDTO> addItem(@Valid @RequestBody StoreCartItemRequest request) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(cartService.addItem(profile, request));
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<StoreCartSummaryDTO> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(cartService.updateQuantity(profile, id, quantity));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<StoreCartSummaryDTO> removeItem(@PathVariable Long id) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(cartService.removeItem(profile, id));
    }

    @DeleteMapping
    public ResponseEntity<StoreCartSummaryDTO> clearCart() {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(cartService.clearCart(profile));
    }
}
