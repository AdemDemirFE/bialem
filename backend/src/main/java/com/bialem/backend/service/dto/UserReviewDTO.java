package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.UserReview} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserReviewDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    @Max(value = 5)
    private Integer rating;

    @Size(max = 2000)
    private String reviewText;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private ProfileDTO reviewer;

    private ProfileDTO reviewedUser;

    private EventDTO event;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
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

    public ProfileDTO getReviewer() {
        return reviewer;
    }

    public void setReviewer(ProfileDTO reviewer) {
        this.reviewer = reviewer;
    }

    public ProfileDTO getReviewedUser() {
        return reviewedUser;
    }

    public void setReviewedUser(ProfileDTO reviewedUser) {
        this.reviewedUser = reviewedUser;
    }

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserReviewDTO)) {
            return false;
        }

        UserReviewDTO userReviewDTO = (UserReviewDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userReviewDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserReviewDTO{" +
            "id=" + getId() +
            ", rating=" + getRating() +
            ", reviewText='" + getReviewText() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", reviewer=" + getReviewer() +
            ", reviewedUser=" + getReviewedUser() +
            ", event=" + getEvent() +
            "}";
    }
}
