package com.bialem.backend.store.service.dto;

import com.bialem.backend.store.domain.enumeration.StoreCouponDiscountType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class StoreCouponDTO implements Serializable {

    private Long id;
    private String code;
    private StoreCouponDiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minimumCartAmount;
    private BigDecimal maximumDiscount;
    private Instant startDate;
    private Instant endDate;
    private Integer usageLimit;
    private Integer perUserLimit;
    private Boolean isActive;
    private Integer usageCount;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public StoreCouponDiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(StoreCouponDiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinimumCartAmount() {
        return minimumCartAmount;
    }

    public void setMinimumCartAmount(BigDecimal minimumCartAmount) {
        this.minimumCartAmount = minimumCartAmount;
    }

    public BigDecimal getMaximumDiscount() {
        return maximumDiscount;
    }

    public void setMaximumDiscount(BigDecimal maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(Integer perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
