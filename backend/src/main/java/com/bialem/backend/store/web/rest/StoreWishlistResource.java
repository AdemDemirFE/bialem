package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.service.StoreWishlistService;
import com.bialem.backend.store.service.dto.StoreProductListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/wishlist")
public class StoreWishlistResource {

    private final StoreWishlistService wishlistService;
    private final AppSupport appSupport;

    public StoreWishlistResource(StoreWishlistService wishlistService, AppSupport appSupport) {
        this.wishlistService = wishlistService;
        this.appSupport = appSupport;
    }

    @GetMapping
    public ResponseEntity<Page<StoreProductListDTO>> getWishlist(Pageable pageable) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(wishlistService.getWishlist(profile, pageable));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addToWishlist(@PathVariable Long productId) {
        Profile profile = appSupport.currentProfile();
        wishlistService.addToWishlist(profile, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId) {
        Profile profile = appSupport.currentProfile();
        wishlistService.removeFromWishlist(profile, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Boolean> isWishlisted(@PathVariable Long productId) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(wishlistService.isWishlisted(profile, productId));
    }
}
