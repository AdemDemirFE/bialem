package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.StoryReactionType;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.StoryReaction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryReactionDTO implements Serializable {

    private Long id;

    @NotNull
    private StoryReactionType reactionType;

    @NotNull
    private Instant createdAt;

    private StoryDTO story;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(StoryReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public StoryDTO getStory() {
        return story;
    }

    public void setStory(StoryDTO story) {
        this.story = story;
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
        if (!(o instanceof StoryReactionDTO)) {
            return false;
        }
        StoryReactionDTO that = (StoryReactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "StoryReactionDTO{" +
            "id=" + getId() +
            ", reactionType='" + getReactionType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
