package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.FollowRequest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FollowRequestDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    private ProfileDTO requester;

    private ProfileDTO targetUser;

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

    public ProfileDTO getRequester() {
        return requester;
    }

    public void setRequester(ProfileDTO requester) {
        this.requester = requester;
    }

    public ProfileDTO getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(ProfileDTO targetUser) {
        this.targetUser = targetUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FollowRequestDTO)) {
            return false;
        }

        FollowRequestDTO followRequestDTO = (FollowRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, followRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FollowRequestDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", requester=" + getRequester() +
            ", targetUser=" + getTargetUser() +
            "}";
    }
}
