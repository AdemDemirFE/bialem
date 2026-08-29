package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.StoryReactionType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StoryReaction.
 */
@Entity
@Table(
    name = "story_reaction",
    uniqueConstraints = { @UniqueConstraint(name = "ux_story_reaction__story_user", columnNames = { "story_id", "user_id" }) }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryReaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private StoryReactionType reactionType;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "views", "communityTargets" }, allowSetters = true)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    public Long getId() {
        return this.id;
    }

    public StoryReaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryReactionType getReactionType() {
        return this.reactionType;
    }

    public StoryReaction reactionType(StoryReactionType reactionType) {
        this.setReactionType(reactionType);
        return this;
    }

    public void setReactionType(StoryReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public StoryReaction createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Story getStory() {
        return this.story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public StoryReaction story(Story story) {
        this.setStory(story);
        return this;
    }

    public Profile getUser() {
        return this.user;
    }

    public void setUser(Profile profile) {
        this.user = profile;
    }

    public StoryReaction user(Profile profile) {
        this.setUser(profile);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryReaction)) {
            return false;
        }
        return getId() != null && getId().equals(((StoryReaction) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "StoryReaction{" +
            "id=" + getId() +
            ", reactionType='" + getReactionType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
