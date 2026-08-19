package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.CommunityModeratorAssistant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityModeratorAssistantDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean canManageGroups;

    @NotNull
    private Boolean canReviewEvents;

    @NotNull
    private Boolean canManageParticipants;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private CommunityDTO community;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCanManageGroups() {
        return canManageGroups;
    }

    public void setCanManageGroups(Boolean canManageGroups) {
        this.canManageGroups = canManageGroups;
    }

    public Boolean getCanReviewEvents() {
        return canReviewEvents;
    }

    public void setCanReviewEvents(Boolean canReviewEvents) {
        this.canReviewEvents = canReviewEvents;
    }

    public Boolean getCanManageParticipants() {
        return canManageParticipants;
    }

    public void setCanManageParticipants(Boolean canManageParticipants) {
        this.canManageParticipants = canManageParticipants;
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
        if (!(o instanceof CommunityModeratorAssistantDTO)) {
            return false;
        }

        CommunityModeratorAssistantDTO communityModeratorAssistantDTO = (CommunityModeratorAssistantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, communityModeratorAssistantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityModeratorAssistantDTO{" +
            "id=" + getId() +
            ", canManageGroups='" + getCanManageGroups() + "'" +
            ", canReviewEvents='" + getCanReviewEvents() + "'" +
            ", canManageParticipants='" + getCanManageParticipants() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", community=" + getCommunity() +
            ", user=" + getUser() +
            "}";
    }
}
