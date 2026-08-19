package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.AllowMessagesFrom;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.AccountPreferences} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountPreferencesDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean discoverable;

    @NotNull
    private Boolean showCity;

    @NotNull
    private Boolean showFollowConnections;

    @NotNull
    private Boolean allowFollows;

    @NotNull
    private Boolean requireFollowApproval;

    @NotNull
    private AllowMessagesFrom allowMessagesFrom;

    @NotNull
    private Boolean notifyEvents;

    @NotNull
    private Boolean notifyCommunities;

    @NotNull
    private Boolean notifySocial;

    @NotNull
    private Boolean notifyAdvantages;

    @NotNull
    private Boolean notifySystem;

    @NotNull
    private Instant updatedAt;

    @NotNull
    private ProfileDTO profile;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public Boolean getShowCity() {
        return showCity;
    }

    public void setShowCity(Boolean showCity) {
        this.showCity = showCity;
    }

    public Boolean getShowFollowConnections() {
        return showFollowConnections;
    }

    public void setShowFollowConnections(Boolean showFollowConnections) {
        this.showFollowConnections = showFollowConnections;
    }

    public Boolean getAllowFollows() {
        return allowFollows;
    }

    public void setAllowFollows(Boolean allowFollows) {
        this.allowFollows = allowFollows;
    }

    public Boolean getRequireFollowApproval() {
        return requireFollowApproval;
    }

    public void setRequireFollowApproval(Boolean requireFollowApproval) {
        this.requireFollowApproval = requireFollowApproval;
    }

    public AllowMessagesFrom getAllowMessagesFrom() {
        return allowMessagesFrom;
    }

    public void setAllowMessagesFrom(AllowMessagesFrom allowMessagesFrom) {
        this.allowMessagesFrom = allowMessagesFrom;
    }

    public Boolean getNotifyEvents() {
        return notifyEvents;
    }

    public void setNotifyEvents(Boolean notifyEvents) {
        this.notifyEvents = notifyEvents;
    }

    public Boolean getNotifyCommunities() {
        return notifyCommunities;
    }

    public void setNotifyCommunities(Boolean notifyCommunities) {
        this.notifyCommunities = notifyCommunities;
    }

    public Boolean getNotifySocial() {
        return notifySocial;
    }

    public void setNotifySocial(Boolean notifySocial) {
        this.notifySocial = notifySocial;
    }

    public Boolean getNotifyAdvantages() {
        return notifyAdvantages;
    }

    public void setNotifyAdvantages(Boolean notifyAdvantages) {
        this.notifyAdvantages = notifyAdvantages;
    }

    public Boolean getNotifySystem() {
        return notifySystem;
    }

    public void setNotifySystem(Boolean notifySystem) {
        this.notifySystem = notifySystem;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountPreferencesDTO)) {
            return false;
        }

        AccountPreferencesDTO accountPreferencesDTO = (AccountPreferencesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, accountPreferencesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountPreferencesDTO{" +
            "id=" + getId() +
            ", discoverable='" + getDiscoverable() + "'" +
            ", showCity='" + getShowCity() + "'" +
            ", showFollowConnections='" + getShowFollowConnections() + "'" +
            ", allowFollows='" + getAllowFollows() + "'" +
            ", requireFollowApproval='" + getRequireFollowApproval() + "'" +
            ", allowMessagesFrom='" + getAllowMessagesFrom() + "'" +
            ", notifyEvents='" + getNotifyEvents() + "'" +
            ", notifyCommunities='" + getNotifyCommunities() + "'" +
            ", notifySocial='" + getNotifySocial() + "'" +
            ", notifyAdvantages='" + getNotifyAdvantages() + "'" +
            ", notifySystem='" + getNotifySystem() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", profile=" + getProfile() +
            "}";
    }
}
