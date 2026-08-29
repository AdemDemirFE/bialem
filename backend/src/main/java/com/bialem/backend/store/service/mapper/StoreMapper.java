package com.bialem.backend.store.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.*;
import com.bialem.backend.store.domain.enumeration.*;
import com.bialem.backend.store.domain.enumeration.StoreReviewStatus;
import com.bialem.backend.store.service.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class StoreMapper {

    private final ObjectMapper objectMapper;

    public StoreMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public StoreCategoryDTO toCategoryDTO(StoreCategory category) {
        if (category == null) return null;
        StoreCategoryDTO dto = new StoreCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setImageUrl(category.getImageUrl());
        dto.setSortOrder(category.getSortOrder());
        dto.setIsActive(category.getIsActive());
        if (category.getParent() != null) {
            dto.setParentId(category.getParent().getId());
            dto.setParentName(category.getParent().getName());
        }
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        return dto;
    }

    public StoreCategoryDTO toCategoryTree(StoreCategory category) {
        StoreCategoryDTO dto = toCategoryDTO(category);
        if (dto == null) return null;
        if (category.getChildren() != null) {
            dto.setChildren(category.getChildren().stream().map(this::toCategoryTree).filter(Objects::nonNull).toList());
        }
        return dto;
    }

    public StoreBrandDTO toBrandDTO(StoreBrand brand) {
        if (brand == null) return null;
        StoreBrandDTO dto = new StoreBrandDTO();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getSlug());
        dto.setDescription(brand.getDescription());
        dto.setLogoUrl(brand.getLogoUrl());
        dto.setIsActive(brand.getIsActive());
        dto.setCreatedAt(brand.getCreatedAt());
        dto.setUpdatedAt(brand.getUpdatedAt());
        return dto;
    }

    public StoreProductImageDTO toProductImageDTO(StoreProductImage image) {
        if (image == null) return null;
        StoreProductImageDTO dto = new StoreProductImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setThumbnailUrl(image.getThumbnailUrl());
        dto.setSortOrder(image.getSortOrder());
        dto.setIsPrimary(image.getIsPrimary());
        dto.setAltText(image.getAltText());
        return dto;
    }

    public StoreProductVariantDTO toProductVariantDTO(StoreProductVariant variant) {
        if (variant == null) return null;
        StoreProductVariantDTO dto = new StoreProductVariantDTO();
        dto.setId(variant.getId());
        dto.setVariantName(variant.getVariantName());
        dto.setSku(variant.getSku());
        dto.setPrice(variant.getPrice());
        dto.setDiscountedPrice(variant.getDiscountedPrice());
        dto.setStockQuantity(variant.getStockQuantity());
        dto.setImageUrl(variant.getImageUrl());
        dto.setIsActive(variant.getIsActive());
        return dto;
    }

    public StoreProductAttributeDTO toProductAttributeDTO(StoreProductAttribute attribute) {
        if (attribute == null) return null;
        StoreProductAttributeDTO dto = new StoreProductAttributeDTO();
        dto.setId(attribute.getId());
        dto.setAttributeKey(attribute.getAttributeKey());
        dto.setAttributeValue(attribute.getAttributeValue());
        dto.setSortOrder(attribute.getSortOrder());
        return dto;
    }

    public StoreProductDTO toProductDTO(StoreProduct product) {
        if (product == null) return null;
        StoreProductDTO dto = new StoreProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setShortDescription(product.getShortDescription());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setPrice(product.getPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setCurrency(product.getCurrency());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setLowStockThreshold(product.getLowStockThreshold());
        dto.setStatus(product.getStatus());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setIsActive(product.getIsActive());
        dto.setWeight(product.getWeight());
        dto.setWidth(product.getWidth());
        dto.setHeight(product.getHeight());
        dto.setLength(product.getLength());
        dto.setRatingAverage(product.getRatingAverage());
        dto.setReviewCount(product.getReviewCount());
        dto.setSalesCount(product.getSalesCount());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
            dto.setCategorySlug(product.getCategory().getSlug());
        }
        if (product.getBrand() != null) {
            dto.setBrandId(product.getBrand().getId());
            dto.setBrandName(product.getBrand().getName());
        }
        if (product.getSeller() != null) {
            dto.setSellerId(product.getSeller().getId());
            dto.setSellerName(product.getSeller().getDisplayName());
        }
        if (product.getImages() != null) {
            dto.setImages(product.getImages().stream().map(this::toProductImageDTO).toList());
        }
        if (product.getVariants() != null) {
            dto.setVariants(product.getVariants().stream().map(this::toProductVariantDTO).toList());
        }
        if (product.getAttributes() != null) {
            dto.setAttributes(product.getAttributes().stream().map(this::toProductAttributeDTO).toList());
        }
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    public StoreProductListDTO toProductListDTO(StoreProduct product) {
        if (product == null) return null;
        StoreProductListDTO dto = new StoreProductListDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSlug(product.getSlug());
        dto.setShortDescription(product.getShortDescription());
        dto.setPrice(product.getPrice());
        dto.setDiscountedPrice(product.getDiscountedPrice());
        dto.setCurrency(product.getCurrency());
        dto.setRatingAverage(product.getRatingAverage());
        dto.setReviewCount(product.getReviewCount());
        dto.setSalesCount(product.getSalesCount());
        dto.setInStock(product.getStockQuantity() != null && product.getStockQuantity() > 0);
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        if (product.getImages() != null) {
            dto.setPrimaryImageUrl(
                product.getImages().stream().filter(StoreProductImage::getIsPrimary).findFirst().map(StoreProductImage::getImageUrl).orElse(
                    product.getImages().stream().findFirst().map(StoreProductImage::getImageUrl).orElse(null)
                )
            );
        }
        return dto;
    }

    public StoreCartItemDTO toCartItemDTO(StoreCartItem item) {
        if (item == null) return null;
        StoreCartItemDTO dto = new StoreCartItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscountAmount(item.getDiscountAmount());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
            dto.setProductSlug(item.getProduct().getSlug());
            dto.setProductImage(primaryImageUrl(item.getProduct()));
            dto.setStockQuantity(item.getProduct().getStockQuantity());
            dto.setInStock(item.getProduct().getStockQuantity() != null && item.getProduct().getStockQuantity() > 0);
        }
        if (item.getVariant() != null) {
            dto.setVariantId(item.getVariant().getId());
            dto.setVariantName(item.getVariant().getVariantName());
            dto.setStockQuantity(item.getVariant().getStockQuantity());
            dto.setInStock(item.getVariant().getStockQuantity() != null && item.getVariant().getStockQuantity() > 0);
        }
        dto.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return dto;
    }

    public StoreCartSummaryDTO toCartSummary(List<StoreCartItem> items) {
        StoreCartSummaryDTO summary = new StoreCartSummaryDTO();
        List<StoreCartItemDTO> dtos = items.stream().map(this::toCartItemDTO).toList();
        summary.setItems(dtos);
        BigDecimal subtotal = dtos.stream().map(StoreCartItemDTO::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setSubtotal(subtotal);
        summary.setDiscountAmount(BigDecimal.ZERO);
        summary.setShippingAmount(BigDecimal.ZERO);
        summary.setTotalAmount(subtotal);
        summary.setItemCount(dtos.stream().mapToInt(StoreCartItemDTO::getQuantity).sum());
        return summary;
    }

    public StoreAddressDTO toAddressDTO(StoreAddress address) {
        if (address == null) return null;
        StoreAddressDTO dto = new StoreAddressDTO();
        dto.setId(address.getId());
        dto.setTitle(address.getTitle());
        dto.setFirstName(address.getFirstName());
        dto.setLastName(address.getLastName());
        dto.setPhone(address.getPhone());
        dto.setCountry(address.getCountry());
        dto.setCity(address.getCity());
        dto.setDistrict(address.getDistrict());
        dto.setNeighborhood(address.getNeighborhood());
        dto.setAddressLine(address.getAddressLine());
        dto.setPostalCode(address.getPostalCode());
        dto.setNote(address.getNote());
        dto.setIsDefault(address.getIsDefault());
        dto.setCreatedAt(address.getCreatedAt());
        dto.setUpdatedAt(address.getUpdatedAt());
        return dto;
    }

    public StoreCouponDTO toCouponDTO(StoreCoupon coupon) {
        if (coupon == null) return null;
        StoreCouponDTO dto = new StoreCouponDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountType(coupon.getDiscountType());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinimumCartAmount(coupon.getMinimumCartAmount());
        dto.setMaximumDiscount(coupon.getMaximumDiscount());
        dto.setStartDate(coupon.getStartDate());
        dto.setEndDate(coupon.getEndDate());
        dto.setUsageLimit(coupon.getUsageLimit());
        dto.setPerUserLimit(coupon.getPerUserLimit());
        dto.setIsActive(coupon.getIsActive());
        dto.setUsageCount(coupon.getUsageCount());
        dto.setCreatedAt(coupon.getCreatedAt());
        return dto;
    }

    public StoreOrderItemDTO toOrderItemDTO(StoreOrderItem item) {
        if (item == null) return null;
        StoreOrderItemDTO dto = new StoreOrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductNameSnapshot());
        dto.setProductSku(item.getProductSkuSnapshot());
        dto.setProductImage(item.getProductImageSnapshot());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setDiscount(item.getDiscount());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setVariantName(item.getVariantSnapshot());
        return dto;
    }

    public StoreOrderDTO toOrderDTO(StoreOrder order) {
        if (order == null) return null;
        StoreOrderDTO dto = new StoreOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingAmount(order.getShippingAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCurrency(order.getCurrency());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setShippingStatus(order.getShippingStatus());
        dto.setCustomerNote(order.getCustomerNote());
        dto.setCouponCode(order.getCouponCode());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    public StoreOrderDetailDTO toOrderDetailDTO(StoreOrder order) {
        if (order == null) return null;
        StoreOrderDetailDTO dto = new StoreOrderDetailDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setSubtotal(order.getSubtotal());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setShippingAmount(order.getShippingAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCurrency(order.getCurrency());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setShippingStatus(order.getShippingStatus());
        dto.setCustomerNote(order.getCustomerNote());
        dto.setCouponCode(order.getCouponCode());
        dto.setShippingAddress(parseAddressSnapshot(order.getShippingAddressSnapshot()));
        dto.setBillingAddress(parseAddressSnapshot(order.getBillingAddressSnapshot()));
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(this::toOrderItemDTO).toList());
        }
        if (order.getPayment() != null) {
            dto.setPayment(toPaymentDTO(order.getPayment()));
        }
        if (order.getShipping() != null) {
            dto.setShipping(toShippingDTO(order.getShipping()));
        }
        if (order.getStatusHistory() != null) {
            dto.setStatusHistory(order.getStatusHistory().stream().map(this::toOrderStatusHistoryDTO).toList());
        }
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }

    public StoreOrderStatusHistoryDTO toOrderStatusHistoryDTO(StoreOrderStatusHistory history) {
        if (history == null) return null;
        StoreOrderStatusHistoryDTO dto = new StoreOrderStatusHistoryDTO();
        dto.setId(history.getId());
        dto.setOldStatus(history.getOldStatus());
        dto.setNewStatus(history.getNewStatus());
        dto.setChangedBy(history.getChangedBy());
        dto.setNote(history.getNote());
        dto.setCreatedAt(history.getCreatedAt());
        return dto;
    }

    public StorePaymentDTO toPaymentDTO(StorePayment payment) {
        if (payment == null) return null;
        StorePaymentDTO dto = new StorePaymentDTO();
        dto.setId(payment.getId());
        dto.setProvider(payment.getProvider());
        dto.setTransactionId(payment.getTransactionId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setStatus(payment.getStatus());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaidAt(payment.getPaidAt());
        dto.setFailureReason(payment.getFailureReason());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    public StoreShippingDTO toShippingDTO(StoreShipping shipping) {
        if (shipping == null) return null;
        StoreShippingDTO dto = new StoreShippingDTO();
        dto.setId(shipping.getId());
        dto.setCarrier(shipping.getCarrier());
        dto.setTrackingNumber(shipping.getTrackingNumber());
        dto.setShippingStatus(shipping.getShippingStatus());
        dto.setShippedAt(shipping.getShippedAt());
        dto.setEstimatedDeliveryDate(shipping.getEstimatedDeliveryDate());
        dto.setDeliveredAt(shipping.getDeliveredAt());
        return dto;
    }

    public StoreReviewImageDTO toReviewImageDTO(StoreReviewImage image) {
        if (image == null) return null;
        StoreReviewImageDTO dto = new StoreReviewImageDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setThumbnailUrl(image.getThumbnailUrl());
        dto.setSortOrder(image.getSortOrder());
        return dto;
    }

    public StoreReviewDTO toReviewDTO(StoreReview review) {
        if (review == null) return null;
        StoreReviewDTO dto = new StoreReviewDTO();
        dto.setId(review.getId());
        if (review.getProduct() != null) {
            dto.setProductId(review.getProduct().getId());
        }
        if (review.getUser() != null) {
            dto.setUserId(review.getUser().getId());
            dto.setUserName(review.getUser().getDisplayName());
            dto.setUserAvatarUrl(review.getUser().getAvatarUrl());
        }
        dto.setOrderId(review.getOrderId());
        dto.setOrderItemId(review.getOrderItemId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setStatus(review.getStatus());
        dto.setHelpfulCount(review.getHelpfulCount());
        if (review.getImages() != null) {
            dto.setImages(review.getImages().stream().map(this::toReviewImageDTO).toList());
        }
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }

    public StoreAddressDTO parseAddressSnapshot(String snapshot) {
        if (snapshot == null || snapshot.isBlank()) return null;
        try {
            return objectMapper.readValue(snapshot, StoreAddressDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public String toAddressSnapshot(StoreAddressDTO dto) {
        if (dto == null) return null;
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String primaryImageUrl(StoreProduct product) {
        if (product.getImages() == null || product.getImages().isEmpty()) return null;
        return product.getImages().stream().filter(StoreProductImage::getIsPrimary).findFirst().map(StoreProductImage::getImageUrl).orElse(
            product.getImages().get(0).getImageUrl()
        );
    }

    public BigDecimal effectivePrice(StoreProduct product) {
        if (product.getDiscountedPrice() != null && product.getDiscountedPrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getDiscountedPrice();
        }
        return product.getPrice();
    }

    public BigDecimal effectivePrice(StoreProduct product, StoreProductVariant variant) {
        if (variant != null && variant.getDiscountedPrice() != null && variant.getDiscountedPrice().compareTo(BigDecimal.ZERO) > 0) {
            return variant.getDiscountedPrice();
        }
        if (variant != null && variant.getPrice() != null && variant.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            return variant.getPrice();
        }
        return effectivePrice(product);
    }

    public int availableStock(StoreProduct product, StoreProductVariant variant) {
        if (variant != null) return variant.getStockQuantity() == null ? 0 : variant.getStockQuantity();
        return product.getStockQuantity() == null ? 0 : product.getStockQuantity();
    }

    public StoreProductStatus fromProductStatus(String status) {
        if (status == null) return null;
        try {
            return StoreProductStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public StorePaymentProviderType fromPaymentProvider(String provider) {
        if (provider == null) return null;
        try {
            return StorePaymentProviderType.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void updateProductFromDTO(StoreProduct product, StoreProductDTO dto) {
        product.setName(dto.getName());
        product.setSlug(dto.getSlug());
        product.setShortDescription(dto.getShortDescription());
        product.setDescription(dto.getDescription());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setPrice(dto.getPrice());
        product.setDiscountedPrice(dto.getDiscountedPrice());
        product.setCurrency(dto.getCurrency());
        product.setStockQuantity(dto.getStockQuantity());
        product.setLowStockThreshold(dto.getLowStockThreshold());
        product.setStatus(dto.getStatus());
        product.setIsFeatured(dto.getIsFeatured());
        product.setIsActive(dto.getIsActive());
        product.setWeight(dto.getWeight());
        product.setWidth(dto.getWidth());
        product.setHeight(dto.getHeight());
        product.setLength(dto.getLength());
    }

    public void updateCategoryFromDTO(StoreCategory category, StoreCategoryDTO dto) {
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setSortOrder(dto.getSortOrder());
        category.setIsActive(dto.getIsActive());
    }

    public void updateBrandFromDTO(StoreBrand brand, StoreBrandDTO dto) {
        brand.setName(dto.getName());
        brand.setSlug(dto.getSlug());
        brand.setDescription(dto.getDescription());
        brand.setLogoUrl(dto.getLogoUrl());
        brand.setIsActive(dto.getIsActive());
    }

    public void updateAddressFromDTO(StoreAddress address, StoreAddressDTO dto) {
        address.setTitle(dto.getTitle());
        address.setFirstName(dto.getFirstName());
        address.setLastName(dto.getLastName());
        address.setPhone(dto.getPhone());
        address.setCountry(dto.getCountry());
        address.setCity(dto.getCity());
        address.setDistrict(dto.getDistrict());
        address.setNeighborhood(dto.getNeighborhood());
        address.setAddressLine(dto.getAddressLine());
        address.setPostalCode(dto.getPostalCode());
        address.setNote(dto.getNote());
        address.setIsDefault(dto.getIsDefault());
    }
}
