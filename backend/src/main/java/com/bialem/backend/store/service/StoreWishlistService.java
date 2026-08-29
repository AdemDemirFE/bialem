package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.StoreProduct;
import com.bialem.backend.store.domain.StoreWishlist;
import com.bialem.backend.store.repository.StoreProductRepository;
import com.bialem.backend.store.repository.StoreWishlistRepository;
import com.bialem.backend.store.service.dto.StoreProductListDTO;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreWishlistService {

    private final StoreWishlistRepository wishlistRepository;
    private final StoreProductRepository productRepository;
    private final StoreMapper mapper;

    public StoreWishlistService(StoreWishlistRepository wishlistRepository, StoreProductRepository productRepository, StoreMapper mapper) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<StoreProductListDTO> getWishlist(Profile user, Pageable pageable) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable).map(w -> mapper.toProductListDTO(w.getProduct()));
    }

    public void addToWishlist(Profile user, Long productId) {
        StoreProduct product = productRepository
            .findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), productId)) {
            StoreWishlist item = new StoreWishlist();
            item.setUser(user);
            item.setProduct(product);
            item.setCreatedAt(Instant.now());
            wishlistRepository.save(item);
        }
    }

    public void removeFromWishlist(Profile user, Long productId) {
        wishlistRepository.findByUserIdAndProductId(user.getId(), productId).ifPresent(wishlistRepository::delete);
    }

    public boolean isWishlisted(Profile user, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(user.getId(), productId);
    }
}
