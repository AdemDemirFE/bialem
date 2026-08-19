package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.CommunityType;
import com.bialem.backend.domain.enumeration.CommunityVisibility;
import com.bialem.backend.domain.enumeration.PartnerTrustLevel;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.Community} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityDTO implements Serializable {

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
    private CommunityVisibility visibility;

    @Size(max = 2048)
    private String coverImageUrl;

    @NotNull
    private CommunityType communityType;

    @NotNull
    private PartnerTrustLevel partnerTrustLevel;

    @NotNull
    private Boolean isVerifiedPartner;

    @NotNull
    private Boolean isDiscoverable;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private CommunityDTO parent;

    private CommunityDTO categoryHub;

    private ProfileDTO createdBy;

    private ProfileDTO leadModerator;

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

    public CommunityVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(CommunityVisibility visibility) {
        this.visibility = visibility;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public CommunityType getCommunityType() {
        return communityType;
    }

    public void setCommunityType(CommunityType communityType) {
        this.communityType = communityType;
    }

    public PartnerTrustLevel getPartnerTrustLevel() {
        return partnerTrustLevel;
    }

    public void setPartnerTrustLevel(PartnerTrustLevel partnerTrustLevel) {
        this.partnerTrustLevel = partnerTrustLevel;
    }

    public Boolean getIsVerifiedPartner() {
        return isVerifiedPartner;
    }

    public void setIsVerifiedPartner(Boolean isVerifiedPartner) {
        this.isVerifiedPartner = isVerifiedPartner;
    }

    public Boolean getIsDiscoverable() {
        return isDiscoverable;
    }

    public void setIsDiscoverable(Boolean isDiscoverable) {
        this.isDiscoverable = isDiscoverable;
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

    public CommunityDTO getParent() {
        return parent;
    }

    public void setParent(CommunityDTO parent) {
        this.parent = parent;
    }

    public CommunityDTO getCategoryHub() {
        return categoryHub;
    }

    public void setCategoryHub(CommunityDTO categoryHub) {
        this.categoryHub = categoryHub;
    }

    public ProfileDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ProfileDTO createdBy) {
        this.createdBy = createdBy;
    }

    public ProfileDTO getLeadModerator() {
        return leadModerator;
    }

    public void setLeadModerator(ProfileDTO leadModerator) {
        this.leadModerator = leadModerator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunityDTO)) {
            return false;
        }

        CommunityDTO communityDTO = (CommunityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, communityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", communityType='" + getCommunityType() + "'" +
            ", partnerTrustLevel='" + getPartnerTrustLevel() + "'" +
            ", isVerifiedPartner='" + getIsVerifiedPartner() + "'" +
            ", isDiscoverable='" + getIsDiscoverable() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", parent=" + getParent() +
            ", categoryHub=" + getCategoryHub() +
            ", createdBy=" + getCreatedBy() +
            ", leadModerator=" + getLeadModerator() +
            "}";
    }
}
