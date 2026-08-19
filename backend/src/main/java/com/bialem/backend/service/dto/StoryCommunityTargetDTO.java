package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.StoryCommunityTarget} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryCommunityTargetDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    private StoryDTO story;

    private CommunityDTO community;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public CommunityDTO getCommunity() {
        return community;
    }

    public void setCommunity(CommunityDTO community) {
        this.community = community;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryCommunityTargetDTO)) {
            return false;
        }

        StoryCommunityTargetDTO storyCommunityTargetDTO = (StoryCommunityTargetDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, storyCommunityTargetDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoryCommunityTargetDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", story=" + getStory() +
            ", community=" + getCommunity() +
            "}";
    }
}
