package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.TicketOfferAvailability;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.CityEventTicketOffer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEventTicketOfferDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String providerCode;

    @NotNull
    @Size(max = 120)
    private String externalOfferId;

    @NotNull
    @Size(max = 160)
    private String sellerName;

    @NotNull
    @Size(max = 2048)
    private String purchaseUrl;

    @Size(max = 8)
    private String currency;

    @DecimalMin(value = "0")
    private BigDecimal minPrice;

    @DecimalMin(value = "0")
    private BigDecimal maxPrice;

    @Size(max = 120)
    private String priceLabel;

    @NotNull
    private TicketOfferAvailability availability;

    private Boolean feesIncluded;

    @NotNull
    private Boolean isOfficial;

    @NotNull
    private Instant lastCheckedAt;

    @Lob
    private String rawPayload;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private CityEventDTO cityEvent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getExternalOfferId() {
        return externalOfferId;
    }

    public void setExternalOfferId(String externalOfferId) {
        this.externalOfferId = externalOfferId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPurchaseUrl() {
        return purchaseUrl;
    }

    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getPriceLabel() {
        return priceLabel;
    }

    public void setPriceLabel(String priceLabel) {
        this.priceLabel = priceLabel;
    }

    public TicketOfferAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(TicketOfferAvailability availability) {
        this.availability = availability;
    }

    public Boolean getFeesIncluded() {
        return feesIncluded;
    }

    public void setFeesIncluded(Boolean feesIncluded) {
        this.feesIncluded = feesIncluded;
    }

    public Boolean getIsOfficial() {
        return isOfficial;
    }

    public void setIsOfficial(Boolean isOfficial) {
        this.isOfficial = isOfficial;
    }

    public Instant getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(Instant lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public String getRawPayload() {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
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

    public CityEventDTO getCityEvent() {
        return cityEvent;
    }

    public void setCityEvent(CityEventDTO cityEvent) {
        this.cityEvent = cityEvent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityEventTicketOfferDTO)) {
            return false;
        }

        CityEventTicketOfferDTO cityEventTicketOfferDTO = (CityEventTicketOfferDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cityEventTicketOfferDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEventTicketOfferDTO{" +
            "id=" + getId() +
            ", providerCode='" + getProviderCode() + "'" +
            ", externalOfferId='" + getExternalOfferId() + "'" +
            ", sellerName='" + getSellerName() + "'" +
            ", purchaseUrl='" + getPurchaseUrl() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", minPrice=" + getMinPrice() +
            ", maxPrice=" + getMaxPrice() +
            ", priceLabel='" + getPriceLabel() + "'" +
            ", availability='" + getAvailability() + "'" +
            ", feesIncluded='" + getFeesIncluded() + "'" +
            ", isOfficial='" + getIsOfficial() + "'" +
            ", lastCheckedAt='" + getLastCheckedAt() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", cityEvent=" + getCityEvent() +
            "}";
    }
}
