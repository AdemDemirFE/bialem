package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.RedemptionStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.bialem.backend.domain.PartnerOfferRedemption} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerOfferRedemptionDTO implements Serializable {

    private Long id;

    @NotNull
    private UUID token;

    @NotNull
    @Size(max = 32)
    private String redemptionCode;

    @NotNull
    private RedemptionStatus status;

    @NotNull
    private Instant issuedAt;

    @NotNull
    private Instant expiresAt;

    private Instant redeemedAt;

    @DecimalMin(value = "0")
    private BigDecimal orderAmount;

    @DecimalMin(value = "0")
    private BigDecimal discountAmount;

    private PartnerOfferDTO offer;

    private PartnerVenueDTO venue;

    private ProfileDTO user;

    private ProfileDTO redeemedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getRedemptionCode() {
        return redemptionCode;
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public RedemptionStatus getStatus() {
        return status;
    }

    public void setStatus(RedemptionStatus status) {
        this.status = status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public PartnerOfferDTO getOffer() {
        return offer;
    }

    public void setOffer(PartnerOfferDTO offer) {
        this.offer = offer;
    }

    public PartnerVenueDTO getVenue() {
        return venue;
    }

    public void setVenue(PartnerVenueDTO venue) {
        this.venue = venue;
    }

    public ProfileDTO getUser() {
        return user;
    }

    public void setUser(ProfileDTO user) {
        this.user = user;
    }

    public ProfileDTO getRedeemedBy() {
        return redeemedBy;
    }

    public void setRedeemedBy(ProfileDTO redeemedBy) {
        this.redeemedBy = redeemedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerOfferRedemptionDTO)) {
            return false;
        }

        PartnerOfferRedemptionDTO partnerOfferRedemptionDTO = (PartnerOfferRedemptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partnerOfferRedemptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerOfferRedemptionDTO{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", redemptionCode='" + getRedemptionCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", redeemedAt='" + getRedeemedAt() + "'" +
            ", orderAmount=" + getOrderAmount() +
            ", discountAmount=" + getDiscountAmount() +
            ", offer=" + getOffer() +
            ", venue=" + getVenue() +
            ", user=" + getUser() +
            ", redeemedBy=" + getRedeemedBy() +
            "}";
    }
}
