package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.AllowMessagesFrom;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AccountPreferences.
 */
@Entity
@Table(name = "account_preferences")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AccountPreferences implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "discoverable", nullable = false)
    private Boolean discoverable;

    @NotNull
    @Column(name = "show_city", nullable = false)
    private Boolean showCity;

    @NotNull
    @Column(name = "show_follow_connections", nullable = false)
    private Boolean showFollowConnections;

    @NotNull
    @Column(name = "allow_follows", nullable = false)
    private Boolean allowFollows;

    @NotNull
    @Column(name = "require_follow_approval", nullable = false)
    private Boolean requireFollowApproval;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "allow_messages_from", nullable = false)
    private AllowMessagesFrom allowMessagesFrom;

    @NotNull
    @Column(name = "notify_events", nullable = false)
    private Boolean notifyEvents;

    @NotNull
    @Column(name = "notify_communities", nullable = false)
    private Boolean notifyCommunities;

    @NotNull
    @Column(name = "notify_social", nullable = false)
    private Boolean notifySocial;

    @NotNull
    @Column(name = "notify_advantages", nullable = false)
    private Boolean notifyAdvantages;

    @NotNull
    @Column(name = "notify_system", nullable = false)
    private Boolean notifySystem;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private Profile profile;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AccountPreferences id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDiscoverable() {
        return this.discoverable;
    }

    public AccountPreferences discoverable(Boolean discoverable) {
        this.setDiscoverable(discoverable);
        return this;
    }

    public void setDiscoverable(Boolean discoverable) {
        this.discoverable = discoverable;
    }

    public Boolean getShowCity() {
        return this.showCity;
    }

    public AccountPreferences showCity(Boolean showCity) {
        this.setShowCity(showCity);
        return this;
    }

    public void setShowCity(Boolean showCity) {
        this.showCity = showCity;
    }

    public Boolean getShowFollowConnections() {
        return this.showFollowConnections;
    }

    public AccountPreferences showFollowConnections(Boolean showFollowConnections) {
        this.setShowFollowConnections(showFollowConnections);
        return this;
    }

    public void setShowFollowConnections(Boolean showFollowConnections) {
        this.showFollowConnections = showFollowConnections;
    }

    public Boolean getAllowFollows() {
        return this.allowFollows;
    }

    public AccountPreferences allowFollows(Boolean allowFollows) {
        this.setAllowFollows(allowFollows);
        return this;
    }

    public void setAllowFollows(Boolean allowFollows) {
        this.allowFollows = allowFollows;
    }

    public Boolean getRequireFollowApproval() {
        return this.requireFollowApproval;
    }

    public AccountPreferences requireFollowApproval(Boolean requireFollowApproval) {
        this.setRequireFollowApproval(requireFollowApproval);
        return this;
    }

    public void setRequireFollowApproval(Boolean requireFollowApproval) {
        this.requireFollowApproval = requireFollowApproval;
    }

    public AllowMessagesFrom getAllowMessagesFrom() {
        return this.allowMessagesFrom;
    }

    public AccountPreferences allowMessagesFrom(AllowMessagesFrom allowMessagesFrom) {
        this.setAllowMessagesFrom(allowMessagesFrom);
        return this;
    }

    public void setAllowMessagesFrom(AllowMessagesFrom allowMessagesFrom) {
        this.allowMessagesFrom = allowMessagesFrom;
    }

    public Boolean getNotifyEvents() {
        return this.notifyEvents;
    }

    public AccountPreferences notifyEvents(Boolean notifyEvents) {
        this.setNotifyEvents(notifyEvents);
        return this;
    }

    public void setNotifyEvents(Boolean notifyEvents) {
        this.notifyEvents = notifyEvents;
    }

    public Boolean getNotifyCommunities() {
        return this.notifyCommunities;
    }

    public AccountPreferences notifyCommunities(Boolean notifyCommunities) {
        this.setNotifyCommunities(notifyCommunities);
        return this;
    }

    public void setNotifyCommunities(Boolean notifyCommunities) {
        this.notifyCommunities = notifyCommunities;
    }

    public Boolean getNotifySocial() {
        return this.notifySocial;
    }

    public AccountPreferences notifySocial(Boolean notifySocial) {
        this.setNotifySocial(notifySocial);
        return this;
    }

    public void setNotifySocial(Boolean notifySocial) {
        this.notifySocial = notifySocial;
    }

    public Boolean getNotifyAdvantages() {
        return this.notifyAdvantages;
    }

    public AccountPreferences notifyAdvantages(Boolean notifyAdvantages) {
        this.setNotifyAdvantages(notifyAdvantages);
        return this;
    }

    public void setNotifyAdvantages(Boolean notifyAdvantages) {
        this.notifyAdvantages = notifyAdvantages;
    }

    public Boolean getNotifySystem() {
        return this.notifySystem;
    }

    public AccountPreferences notifySystem(Boolean notifySystem) {
        this.setNotifySystem(notifySystem);
        return this;
    }

    public void setNotifySystem(Boolean notifySystem) {
        this.notifySystem = notifySystem;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public AccountPreferences updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public AccountPreferences profile(Profile profile) {
        this.setProfile(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountPreferences)) {
            return false;
        }
        return getId() != null && getId().equals(((AccountPreferences) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AccountPreferences{" +
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
            "}";
    }
}
