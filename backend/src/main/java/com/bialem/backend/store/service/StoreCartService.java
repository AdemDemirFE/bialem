package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.StoreCartItem;
import com.bialem.backend.store.domain.StoreProduct;
import com.bialem.backend.store.domain.StoreProductVariant;
import com.bialem.backend.store.domain.enumeration.StoreProductStatus;
import com.bialem.backend.store.repository.StoreCartItemRepository;
import com.bialem.backend.store.repository.StoreProductRepository;
import com.bialem.backend.store.repository.StoreProductVariantRepository;
import com.bialem.backend.store.service.dto.StoreCartItemRequest;
import com.bialem.backend.store.service.dto.StoreCartSummaryDTO;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreCartService {

    private final StoreCartItemRepository cartItemRepository;
    private final StoreProductRepository productRepository;
    private final StoreProductVariantRepository variantRepository;
    private final StoreMapper mapper;

    public StoreCartService(
        StoreCartItemRepository cartItemRepository,
        StoreProductRepository productRepository,
        StoreProductVariantRepository variantRepository,
        StoreMapper mapper
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.mapper = mapper;
    }

    public StoreCartSummaryDTO getCart(Profile user) {
        List<StoreCartItem> items = cartItemRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return mapper.toCartSummary(items);
    }

    public StoreCartSummaryDTO addItem(Profile user, StoreCartItemRequest request) {
        StoreProduct product = productRepository
            .findByIdAndDeletedAtIsNull(request.getProductId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        if (!StoreProductStatus.ACTIVE.equals(product.getStatus()) || Boolean.FALSE.equals(product.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ürün satışa açık değil");
        }
        StoreProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = variantRepository
                .findByIdAndProductId(request.getVariantId(), product.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Varyant bulunamadı"));
            if (Boolean.FALSE.equals(variant.getIsActive())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Varyant satışa açık değil");
            }
        }
        int available = mapper.availableStock(product, variant);
        if (available < request.getQuantity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yeterli stok yok");
        }
        java.math.BigDecimal unitPrice = mapper.effectivePrice(product, variant);

        Optional<StoreCartItem> existing = cartItemRepository.findByUserIdAndProductIdAndVariantId(
            user.getId(),
            product.getId(),
            variant == null ? null : variant.getId()
        );
        StoreCartItem item;
        if (existing.isPresent()) {
            item = existing.get();
            int newQty = item.getQuantity() + request.getQuantity();
            if (available < newQty) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yeterli stok yok");
            }
            item.setQuantity(newQty);
            item.setUpdatedAt(Instant.now());
        } else {
            item = new StoreCartItem();
            item.setUser(user);
            item.setProduct(product);
            item.setVariant(variant);
            item.setQuantity(request.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setDiscountAmount(java.math.BigDecimal.ZERO);
            item.setCreatedAt(Instant.now());
        }
        cartItemRepository.save(item);
        return getCart(user);
    }

    public StoreCartSummaryDTO updateQuantity(Profile user, Long cartItemId, Integer quantity) {
        StoreCartItem item = cartItemRepository
            .findByIdAndUserId(cartItemId, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sepet öğesi bulunamadı"));
        if (quantity == null || quantity < 1) {
            cartItemRepository.deleteByUserIdAndId(user.getId(), cartItemId);
            return getCart(user);
        }
        int available = mapper.availableStock(item.getProduct(), item.getVariant());
        if (available < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yeterli stok yok");
        }
        item.setQuantity(quantity);
        item.setUpdatedAt(Instant.now());
        return getCart(user);
    }

    public StoreCartSummaryDTO removeItem(Profile user, Long cartItemId) {
        cartItemRepository.deleteByUserIdAndId(user.getId(), cartItemId);
        return getCart(user);
    }

    public StoreCartSummaryDTO clearCart(Profile user) {
        cartItemRepository.deleteByUserId(user.getId());
        return getCart(user);
    }
}
