package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.PartnerVenueCategory;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PartnerVenue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerVenueDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 160)
    private String name;

    @NotNull
    @Size(max = 80)
    private String slug;

    @Lob
    private String description;

    @NotNull
    private PartnerVenueCategory category;

    @Size(max = 2048)
    private String logoUrl;

    @Size(max = 2048)
    private String coverImageUrl;

    @NotNull
    @Size(max = 500)
    private String address;

    @NotNull
    @Size(max = 80)
    private String city;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Size(max = 40)
    private String phone;

    @Size(max = 2048)
    private String websiteUrl;

    @Size(max = 2048)
    private String instagramUrl;

    @NotNull
    private Boolean isFeatured;

    @NotNull
    private Boolean isActive;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PartnerVenueCategory getCategory() {
        return category;
    }

    public void setCategory(PartnerVenueCategory category) {
        this.category = category;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
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
        if (!(o instanceof PartnerVenueDTO)) {
            return false;
        }

        PartnerVenueDTO partnerVenueDTO = (PartnerVenueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partnerVenueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerVenueDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", phone='" + getPhone() + "'" +
            ", websiteUrl='" + getWebsiteUrl() + "'" +
            ", instagramUrl='" + getInstagramUrl() + "'" +
            ", isFeatured='" + getIsFeatured() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
