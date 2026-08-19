package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.GroupModerationStatus;
import com.bialem.backend.domain.enumeration.PlatformModerationStatus;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.Event} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String title;

    @Lob
    private String description;

    @NotNull
    private Instant startsAt;

    private Instant endsAt;

    @Size(max = 200)
    private String locationName;

    @Size(max = 500)
    private String addressText;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @Size(max = 2048)
    private String coverImageUrl;

    @Min(value = 1)
    private Integer capacity;

    @NotNull
    private EventStatus status;

    @Size(max = 1000)
    private String rejectionReason;

    private Instant publishedAt;

    @NotNull
    private Boolean publishedToDiscovery;

    @NotNull
    private GroupModerationStatus groupModerationStatus;

    @NotNull
    private PlatformModerationStatus platformModerationStatus;

    private Instant cancelledAt;

    @Size(max = 1000)
    private String cancellationReason;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private CommunityDTO community;

    private CommunityDTO category;

    private ProfileDTO createdBy;

    private ProfileDTO cancelledBy;

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

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean getPublishedToDiscovery() {
        return publishedToDiscovery;
    }

    public void setPublishedToDiscovery(Boolean publishedToDiscovery) {
        this.publishedToDiscovery = publishedToDiscovery;
    }

    public GroupModerationStatus getGroupModerationStatus() {
        return groupModerationStatus;
    }

    public void setGroupModerationStatus(GroupModerationStatus groupModerationStatus) {
        this.groupModerationStatus = groupModerationStatus;
    }

    public PlatformModerationStatus getPlatformModerationStatus() {
        return platformModerationStatus;
    }

    public void setPlatformModerationStatus(PlatformModerationStatus platformModerationStatus) {
        this.platformModerationStatus = platformModerationStatus;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
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

    public CommunityDTO getCommunity() {
        return community;
    }

    public void setCommunity(CommunityDTO community) {
        this.community = community;
    }

    public CommunityDTO getCategory() {
        return category;
    }

    public void setCategory(CommunityDTO category) {
        this.category = category;
    }

    public ProfileDTO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(ProfileDTO createdBy) {
        this.createdBy = createdBy;
    }

    public ProfileDTO getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(ProfileDTO cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventDTO)) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", endsAt='" + getEndsAt() + "'" +
            ", locationName='" + getLocationName() + "'" +
            ", addressText='" + getAddressText() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", capacity=" + getCapacity() +
            ", status='" + getStatus() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            ", publishedToDiscovery='" + getPublishedToDiscovery() + "'" +
            ", groupModerationStatus='" + getGroupModerationStatus() + "'" +
            ", platformModerationStatus='" + getPlatformModerationStatus() + "'" +
            ", cancelledAt='" + getCancelledAt() + "'" +
            ", cancellationReason='" + getCancellationReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", community=" + getCommunity() +
            ", category=" + getCategory() +
            ", createdBy=" + getCreatedBy() +
            ", cancelledBy=" + getCancelledBy() +
            "}";
    }
}
