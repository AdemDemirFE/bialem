package com.bialem.backend.store.domain;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.enumeration.StoreProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StoreProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 300)
    @Column(name = "name", nullable = false, length = 300)
    private String name;

    @NotNull
    @Size(max = 300)
    @Column(name = "slug", nullable = false, unique = true, length = 300)
    private String slug;

    @Size(max = 500)
    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Size(max = 120)
    @Column(name = "sku", length = 120, unique = true)
    private String sku;

    @Size(max = 120)
    @Column(name = "barcode", length = 120)
    private String barcode;

    @NotNull
    @Column(name = "price", nullable = false, precision = 21, scale = 2)
    private BigDecimal price;

    @Column(name = "discounted_price", precision = 21, scale = 2)
    private BigDecimal discountedPrice;

    @NotNull
    @Size(max = 8)
    @Column(name = "currency", nullable = false, length = 8)
    private String currency = "TRY";

    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold = 5;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StoreProductStatus status = StoreProductStatus.DRAFT;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "weight", precision = 10, scale = 3)
    private BigDecimal weight;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "rating_average", precision = 3, scale = 2)
    private BigDecimal ratingAverage = BigDecimal.ZERO;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "sales_count")
    private Integer salesCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties(value = { "parent", "children" }, allowSetters = true)
    private StoreCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    @JsonIgnoreProperties(value = {}, allowSetters = true)
    private StoreBrand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile seller;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private List<StoreProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private List<StoreProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private List<StoreProductAttribute> attributes = new ArrayList<>();

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 80)
    @Column(name = "created_by", length = 80)
    private String createdBy;

    @Size(max = 80)
    @Column(name = "updated_by", length = 80)
    private String updatedBy;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public StoreProductStatus getStatus() {
        return status;
    }

    public void setStatus(StoreProductStatus status) {
        this.status = status;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(BigDecimal ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }

    public StoreCategory getCategory() {
        return category;
    }

    public void setCategory(StoreCategory category) {
        this.category = category;
    }

    public StoreBrand getBrand() {
        return brand;
    }

    public void setBrand(StoreBrand brand) {
        this.brand = brand;
    }

    public Profile getSeller() {
        return seller;
    }

    public void setSeller(Profile seller) {
        this.seller = seller;
    }

    public List<StoreProductImage> getImages() {
        return images;
    }

    public void setImages(List<StoreProductImage> images) {
        this.images = images;
    }

    public List<StoreProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<StoreProductVariant> variants) {
        this.variants = variants;
    }

    public List<StoreProductAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<StoreProductAttribute> attributes) {
        this.attributes = attributes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }
}
