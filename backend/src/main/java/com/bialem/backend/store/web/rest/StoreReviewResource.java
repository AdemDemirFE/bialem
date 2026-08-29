package com.bialem.backend.store.web.rest;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.service.AppSupport;
import com.bialem.backend.store.domain.enumeration.StoreReviewStatus;
import com.bialem.backend.store.service.StoreReviewService;
import com.bialem.backend.store.service.dto.StoreReviewDTO;
import com.bialem.backend.store.service.dto.StoreReviewRequest;
import com.bialem.backend.store.service.StoreReviewService.StoreReviewRatingSummary;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/products/{productId}/reviews")
public class StoreReviewResource {

    private final StoreReviewService reviewService;
    private final AppSupport appSupport;

    public StoreReviewResource(StoreReviewService reviewService, AppSupport appSupport) {
        this.reviewService = reviewService;
        this.appSupport = appSupport;
    }

    @GetMapping
    public ResponseEntity<Page<StoreReviewDTO>> getReviews(@PathVariable Long productId, @RequestParam(required = false) Integer rating, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, rating, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<StoreReviewRatingSummary> getRatingSummary(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getRatingSummary(productId));
    }

    @PostMapping
    public ResponseEntity<StoreReviewDTO> createReview(@PathVariable Long productId, @Valid @RequestBody StoreReviewRequest request) {
        Profile profile = appSupport.currentProfile();
        return ResponseEntity.ok(reviewService.createReview(profile, productId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    @PostMapping("/{reviewId}/moderate")
    public ResponseEntity<StoreReviewDTO> moderateReview(@PathVariable Long reviewId, @RequestParam StoreReviewStatus status) {
        return ResponseEntity.ok(reviewService.moderateReview(reviewId, status));
    }
}
