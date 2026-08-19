package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.Block} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BlockDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant createdAt;

    private ProfileDTO blocker;

    private ProfileDTO blockedUser;

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

    public ProfileDTO getBlocker() {
        return blocker;
    }

    public void setBlocker(ProfileDTO blocker) {
        this.blocker = blocker;
    }

    public ProfileDTO getBlockedUser() {
        return blockedUser;
    }

    public void setBlockedUser(ProfileDTO blockedUser) {
        this.blockedUser = blockedUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BlockDTO)) {
            return false;
        }

        BlockDTO blockDTO = (BlockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, blockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BlockDTO{" +
            "id=" + getId() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", blocker=" + getBlocker() +
            ", blockedUser=" + getBlockedUser() +
            "}";
    }
}
