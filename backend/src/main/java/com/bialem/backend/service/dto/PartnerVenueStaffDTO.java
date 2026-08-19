package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PartnerVenueStaff} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerVenueStaffDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant createdAt;

    private PartnerVenueDTO venue;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerVenueStaffDTO)) {
            return false;
        }

        PartnerVenueStaffDTO partnerVenueStaffDTO = (PartnerVenueStaffDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, partnerVenueStaffDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerVenueStaffDTO{" +
            "id=" + getId() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", venue=" + getVenue() +
            ", user=" + getUser() +
            "}";
    }
}
