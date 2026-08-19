package com.bialem.backend.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PartnerOffer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerOfferDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String title;

    @Lob
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal discountPercent;

    @DecimalMin(value = "0")
    private BigDecimal minimumSpend;

    @DecimalMin(value = "0")
    private BigDecimal maximumDiscount;

    @NotNull
    private Instant validFrom;

    private Instant validUntil;

    @Size(max = 32)
    private String validDays;

    private LocalTime dailyStartTime;

    private LocalTime dailyEndTime;

    @Min(value = 1)
    private Integer perUserLimit;

    @Lob
    private String terms;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private PartnerVenueDTO venue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getMinimumSpend() {
        return minimumSpend;
    }

    public void setMinimumSpend(BigDecimal minimumSpend) {
        this.minimumSpend = minimumSpend;
    }

    public BigDecimal getMaximumDiscount() {
        return maximumDiscount;
    }

    public void setMaximumDiscount(BigDecimal maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public String getValidDays() {
        return validDays;
    }

    public void setValidDays(String validDays) {
        this.validDays = validDays;
    }

    public LocalTime getDailyStartTime() {
        return dailyStartTime;
    }

    public void setDailyStartTime(LocalTime dailyStartTime) {
        this.dailyStartTime = dailyStartTime;
    }

    public LocalTime getDailyEndTime() {
        return dailyEndTime;
    }

    public void setDailyEndTime(LocalTime dailyEndTime) {
        this.dailyEndTime = dailyEndTime;
    }

    public Integer getPerUserLimit() {
        return perUserLimit;
    }

    public void setPerUserLimit(Integer perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public PartnerVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(PartnerVenueDTO venue) {
        this.venue = venue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerOfferDTO)) {
            return false;
        }

        PartnerOfferDTO partnerOfferDTO = (PartnerOfferDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partnerOfferDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerOfferDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", discountPercent=" + getDiscountPercent() +
            ", minimumSpend=" + getMinimumSpend() +
            ", maximumDiscount=" + getMaximumDiscount() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", validDays='" + getValidDays() + "'" +
            ", dailyStartTime='" + getDailyStartTime() + "'" +
            ", dailyEndTime='" + getDailyEndTime() + "'" +
            ", perUserLimit=" + getPerUserLimit() +
            ", terms='" + getTerms() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", venue=" + getVenue() +
            "}";
    }
}
