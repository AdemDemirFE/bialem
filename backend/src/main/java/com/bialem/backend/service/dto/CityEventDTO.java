package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.CityEventStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.CityEvent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String title;

    @Lob
    private String description;

    @NotNull
    @Size(max = 80)
    private String category;

    @NotNull
    @Size(max = 80)
    private String city;

    @Size(max = 200)
    private String venueName;

    @Size(max = 500)
    private String addressText;

    @NotNull
    private Instant startsAt;

    private Instant endsAt;

    @Size(max = 2048)
    private String coverImageUrl;

    @Size(max = 120)
    private String priceLabel;

    @NotNull
    @Size(max = 120)
    private String sourceName;

    @Size(max = 2048)
    private String sourceUrl;

    @Size(max = 2048)
    private String ticketUrl;

    @NotNull
    private CityEventStatus status;

    @NotNull
    @Size(max = 50)
    private String providerCode;

    @Size(max = 120)
    private String externalId;

    private Instant lastSyncedAt;

    @Lob
    private String rawPayload;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public Instant getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getPriceLabel() {
        return priceLabel;
    }

    public void setPriceLabel(String priceLabel) {
        this.priceLabel = priceLabel;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTicketUrl() {
        return ticketUrl;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public CityEventStatus getStatus() {
        return status;
    }

    public void setStatus(CityEventStatus status) {
        this.status = status;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityEventDTO)) {
            return false;
        }

        CityEventDTO cityEventDTO = (CityEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cityEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEventDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", city='" + getCity() + "'" +
            ", venueName='" + getVenueName() + "'" +
            ", addressText='" + getAddressText() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", endsAt='" + getEndsAt() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", priceLabel='" + getPriceLabel() + "'" +
            ", sourceName='" + getSourceName() + "'" +
            ", sourceUrl='" + getSourceUrl() + "'" +
            ", ticketUrl='" + getTicketUrl() + "'" +
            ", status='" + getStatus() + "'" +
            ", providerCode='" + getProviderCode() + "'" +
            ", externalId='" + getExternalId() + "'" +
            ", lastSyncedAt='" + getLastSyncedAt() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
