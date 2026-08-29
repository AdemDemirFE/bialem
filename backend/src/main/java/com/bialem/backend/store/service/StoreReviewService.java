package com.bialem.backend.store.service;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.StoreOrder;
import com.bialem.backend.store.domain.StoreOrderItem;
import com.bialem.backend.store.domain.StoreProduct;
import com.bialem.backend.store.domain.StoreReview;
import com.bialem.backend.store.domain.StoreReviewImage;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
import com.bialem.backend.store.domain.enumeration.StoreReviewStatus;
import com.bialem.backend.store.repository.StoreOrderRepository;
import com.bialem.backend.store.repository.StoreProductRepository;
import com.bialem.backend.store.repository.StoreReviewRepository;
import com.bialem.backend.store.service.dto.StoreReviewDTO;
import com.bialem.backend.store.service.dto.StoreReviewRequest;
import com.bialem.backend.store.service.mapper.StoreMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class StoreReviewService {

    private final StoreReviewRepository reviewRepository;
    private final StoreOrderRepository orderRepository;
    private final StoreProductRepository productRepository;
    private final StoreMapper mapper;

    public StoreReviewService(
        StoreReviewRepository reviewRepository,
        StoreOrderRepository orderRepository,
        StoreProductRepository productRepository,
        StoreMapper mapper
    ) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<StoreReviewDTO> getProductReviews(Long productId, Integer rating, Pageable pageable) {
        if (rating != null) {
            return reviewRepository
                .findByProductIdAndStatusAndRatingOrderByCreatedAtDesc(productId, StoreReviewStatus.APPROVED, rating, pageable)
                .map(mapper::toReviewDTO);
        }
        return reviewRepository
            .findByProductIdAndStatusOrderByCreatedAtDesc(productId, StoreReviewStatus.APPROVED, pageable)
            .map(mapper::toReviewDTO);
    }

    @Transactional(readOnly = true)
    public StoreReviewRatingSummary getRatingSummary(Long productId) {
        Object[][] agg = reviewRepository.aggregateRatingByProductIdAndStatus(productId, StoreReviewStatus.APPROVED);
        BigDecimal average = BigDecimal.ZERO;
        Long count = 0L;
        if (agg != null && agg.length > 0 && agg[0] != null) {
            average = agg[0][0] instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : BigDecimal.ZERO;
            count = agg[0][1] instanceof Number n ? n.longValue() : 0L;
        }
        Object[][] distribution = reviewRepository.countByProductIdAndStatusGroupByRating(productId, StoreReviewStatus.APPROVED);
        java.util.Map<Integer, Long> dist = new java.util.HashMap<>();
        for (int i = 1; i <= 5; i++) dist.put(i, 0L);
        if (distribution != null) {
            for (Object[] row : distribution) {
                if (row[0] instanceof Number rating && row[1] instanceof Number c) {
                    dist.put(rating.intValue(), c.longValue());
                }
            }
        }
        return new StoreReviewRatingSummary(average.setScale(2, RoundingMode.HALF_UP), count, dist);
    }

    public StoreReviewDTO createReview(Profile user, Long productId, StoreReviewRequest request) {
        StoreOrderItem matchedItem = null;
        StoreOrder matchedOrder = null;
        List<StoreOrder> deliveredOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), Pageable.unpaged()).getContent();
        outer:
        for (StoreOrder order : deliveredOrders) {
            if (order.getOrderStatus() == StoreOrderStatus.DELIVERED) {
                for (StoreOrderItem item : order.getItems()) {
                    if (item.getProductId().equals(productId) && item.getId().equals(request.getOrderItemId())) {
                        matchedItem = item;
                        matchedOrder = order;
                        break outer;
                    }
                }
            }
        }
        if (matchedItem == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu ürünü değerlendirmek için teslim edilmiş siparişe sahip olmalısınız");
        }
        if (reviewRepository.existsByUserIdAndOrderItemId(user.getId(), matchedItem.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu ürün için zaten değerlendirme yaptınız");
        }
        StoreProduct product = productRepository
            .findByIdAndDeletedAtIsNull(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        StoreReview review = new StoreReview();
        review.setProduct(product);
        review.setUser(user);
        review.setOrderId(matchedOrder.getId());
        review.setOrderItemId(matchedItem.getId());
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setStatus(StoreReviewStatus.PENDING);
        review.setHelpfulCount(0);
        review.setCreatedAt(Instant.now());
        review.setImages(new ArrayList<>());
        if (request.getImageUrls() != null) {
            int sort = 0;
            for (String url : request.getImageUrls()) {
                StoreReviewImage image = new StoreReviewImage();
                image.setReview(review);
                image.setImageUrl(url);
                image.setSortOrder(sort++);
                image.setCreatedAt(Instant.now());
                review.getImages().add(image);
            }
        }
        reviewRepository.save(review);
        recalculateProductRating(product);
        return mapper.toReviewDTO(review);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN','ROLE_STORE_MANAGER','ROLE_STORE_ADMIN')")
    public StoreReviewDTO moderateReview(Long reviewId, StoreReviewStatus status) {
        StoreReview review = reviewRepository
            .findById(reviewId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Değerlendirme bulunamadı"));
        review.setStatus(status);
        review.setUpdatedAt(Instant.now());
        recalculateProductRating(review.getProduct());
        return mapper.toReviewDTO(review);
    }

    private void recalculateProductRating(StoreProduct product) {
        Object[][] agg = reviewRepository.aggregateRatingByProductIdAndStatus(product.getId(), StoreReviewStatus.APPROVED);
        if (agg != null && agg.length > 0 && agg[0] != null) {
            BigDecimal avg = agg[0][0] instanceof Number n ? BigDecimal.valueOf(n.doubleValue()) : BigDecimal.ZERO;
            Long count = agg[0][1] instanceof Number n ? n.longValue() : 0L;
            product.setRatingAverage(avg.setScale(2, RoundingMode.HALF_UP));
            product.setReviewCount(count.intValue());
            product.setUpdatedAt(Instant.now());
        }
    }

    public record StoreReviewRatingSummary(BigDecimal average, Long count, java.util.Map<Integer, Long> distribution) {}
}
