package com.bialem.backend.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "store_order_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StoreOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @NotNull
    @Size(max = 300)
    @Column(name = "product_name_snapshot", nullable = false, length = 300)
    private String productNameSnapshot;

    @Size(max = 120)
    @Column(name = "product_sku_snapshot", length = 120)
    private String productSkuSnapshot;

    @Size(max = 2048)
    @Column(name = "product_image_snapshot", length = 2048)
    private String productImageSnapshot;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "unit_price", nullable = false, precision = 21, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount", precision = 21, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "total_price", nullable = false, precision = 21, scale = 2)
    private BigDecimal totalPrice;

    @Size(max = 1000)
    @Column(name = "variant_snapshot", length = 1000)
    private String variantSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties(value = { "items", "statusHistory", "payment", "shipping" }, allowSetters = true)
    private StoreOrder order;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public String getProductSkuSnapshot() {
        return productSkuSnapshot;
    }

    public void setProductSkuSnapshot(String productSkuSnapshot) {
        this.productSkuSnapshot = productSkuSnapshot;
    }

    public String getProductImageSnapshot() {
        return productImageSnapshot;
    }

    public void setProductImageSnapshot(String productImageSnapshot) {
        this.productImageSnapshot = productImageSnapshot;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getVariantSnapshot() {
        return variantSnapshot;
    }

    public void setVariantSnapshot(String variantSnapshot) {
        this.variantSnapshot = variantSnapshot;
    }

    public StoreOrder getOrder() {
        return order;
    }

    public void setOrder(StoreOrder order) {
        this.order = order;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
