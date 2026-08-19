package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.StoryView} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryViewDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant viewedAt;

    private StoryDTO story;

    private ProfileDTO viewer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(Instant viewedAt) {
        this.viewedAt = viewedAt;
    }

    public StoryDTO getStory() {
        return story;
    }

    public void setStory(StoryDTO story) {
        this.story = story;
    }

    public ProfileDTO getViewer() {
        return viewer;
    }

    public void setViewer(ProfileDTO viewer) {
        this.viewer = viewer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryViewDTO)) {
            return false;
        }

        StoryViewDTO storyViewDTO = (StoryViewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, storyViewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StoryViewDTO{" +
            "id=" + getId() +
            ", viewedAt='" + getViewedAt() + "'" +
            ", story=" + getStory() +
            ", viewer=" + getViewer() +
            "}";
    }
}
