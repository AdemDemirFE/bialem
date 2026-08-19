package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.Follow} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FollowDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    private ProfileDTO follower;

    private ProfileDTO followed;

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

    public ProfileDTO getFollower() {
        return follower;
    }

    public void setFollower(ProfileDTO follower) {
        this.follower = follower;
    }

    public ProfileDTO getFollowed() {
        return followed;
    }

    public void setFollowed(ProfileDTO followed) {
        this.followed = followed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FollowDTO)) {
            return false;
        }

        FollowDTO followDTO = (FollowDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, followDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", follower=" + getFollower() +
            ", followed=" + getFollowed() +
            "}";
    }
}
