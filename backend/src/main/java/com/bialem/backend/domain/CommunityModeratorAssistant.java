package com.bialem.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CommunityModeratorAssistant.
 */
@Entity
@Table(name = "community_moderator_assistant")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityModeratorAssistant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "can_manage_groups", nullable = false)
    private Boolean canManageGroups;

    @NotNull
    @Column(name = "can_review_events", nullable = false)
    private Boolean canReviewEvents;

    @NotNull
    @Column(name = "can_manage_participants", nullable = false)
    private Boolean canManageParticipants;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "parent",
            "categoryHub",
            "createdBy",
            "leadModerator",
            "children",
            "categorizedGroups",
            "members",
            "assistants",
            "events",
            "posts",
            "storyTargets",
        },
        allowSetters = true
    )
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CommunityModeratorAssistant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCanManageGroups() {
        return this.canManageGroups;
    }

    public CommunityModeratorAssistant canManageGroups(Boolean canManageGroups) {
        this.setCanManageGroups(canManageGroups);
        return this;
    }

    public void setCanManageGroups(Boolean canManageGroups) {
        this.canManageGroups = canManageGroups;
    }

    public Boolean getCanReviewEvents() {
        return this.canReviewEvents;
    }

    public CommunityModeratorAssistant canReviewEvents(Boolean canReviewEvents) {
        this.setCanReviewEvents(canReviewEvents);
        return this;
    }

    public void setCanReviewEvents(Boolean canReviewEvents) {
        this.canReviewEvents = canReviewEvents;
    }

    public Boolean getCanManageParticipants() {
        return this.canManageParticipants;
    }

    public CommunityModeratorAssistant canManageParticipants(Boolean canManageParticipants) {
        this.setCanManageParticipants(canManageParticipants);
        return this;
    }

    public void setCanManageParticipants(Boolean canManageParticipants) {
        this.canManageParticipants = canManageParticipants;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public CommunityModeratorAssistant createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CommunityModeratorAssistant updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Community getCommunity() {
        return this.community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public CommunityModeratorAssistant community(Community community) {
        this.setCommunity(community);
        return this;
    }

    public Profile getUser() {
        return this.user;
    }

    public void setUser(Profile profile) {
        this.user = profile;
    }

    public CommunityModeratorAssistant user(Profile profile) {
        this.setUser(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunityModeratorAssistant)) {
            return false;
        }
        return getId() != null && getId().equals(((CommunityModeratorAssistant) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityModeratorAssistant{" +
            "id=" + getId() +
            ", canManageGroups='" + getCanManageGroups() + "'" +
            ", canReviewEvents='" + getCanReviewEvents() + "'" +
            ", canManageParticipants='" + getCanManageParticipants() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
