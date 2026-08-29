package com.bialem.backend.store.domain;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.store.domain.enumeration.StoreOrderStatus;
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
@Table(name = "store_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class StoreOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 120)
    @Column(name = "order_number", nullable = false, unique = true, length = 120)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    @Column(name = "shipping_address_snapshot", columnDefinition = "text")
    private String shippingAddressSnapshot;

    @Column(name = "billing_address_snapshot", columnDefinition = "text")
    private String billingAddressSnapshot;

    @NotNull
    @Column(name = "subtotal", nullable = false, precision = 21, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_amount", precision = 21, scale = 2)
    private BigDecimal shippingAmount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 21, scale = 2)
    private BigDecimal totalAmount;

    @NotNull
    @Size(max = 8)
    @Column(name = "currency", nullable = false, length = 8)
    private String currency = "TRY";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private StoreOrderStatus paymentStatus = StoreOrderStatus.PENDING_PAYMENT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private StoreOrderStatus orderStatus = StoreOrderStatus.PENDING_PAYMENT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false)
    private StoreOrderStatus shippingStatus = StoreOrderStatus.PENDING_PAYMENT;

    @Size(max = 2000)
    @Column(name = "customer_note", length = 2000)
    private String customerNote;

    @Size(max = 120)
    @Column(name = "coupon_code", length = 120)
    private String couponCode;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private List<StoreOrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private List<StoreOrderStatusHistory> statusHistory = new ArrayList<>();

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private StorePayment payment;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "order" }, allowSetters = true)
    private StoreShipping shipping;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    public String getShippingAddressSnapshot() {
        return shippingAddressSnapshot;
    }

    public void setShippingAddressSnapshot(String shippingAddressSnapshot) {
        this.shippingAddressSnapshot = shippingAddressSnapshot;
    }

    public String getBillingAddressSnapshot() {
        return billingAddressSnapshot;
    }

    public void setBillingAddressSnapshot(String billingAddressSnapshot) {
        this.billingAddressSnapshot = billingAddressSnapshot;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public StoreOrderStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(StoreOrderStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public StoreOrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(StoreOrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public StoreOrderStatus getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(StoreOrderStatus shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public List<StoreOrderItem> getItems() {
        return items;
    }

    public void setItems(List<StoreOrderItem> items) {
        this.items = items;
    }

    public List<StoreOrderStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StoreOrderStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public StorePayment getPayment() {
        return payment;
    }

    public void setPayment(StorePayment payment) {
        this.payment = payment;
    }

    public StoreShipping getShipping() {
        return shipping;
    }

    public void setShipping(StoreShipping shipping) {
        this.shipping = shipping;
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
}
