package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.UserHonorBadge} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserHonorBadgeDTO implements Serializable {

    private Long id;

    @Size(max = 500)
    private String reason;

    @NotNull
    private Instant awardedAt;

    private ProfileDTO user;

    private HonorBadgeDTO badge;

    private ProfileDTO awardedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(Instant awardedAt) {
        this.awardedAt = awardedAt;
    }

    public ProfileDTO getUser() {
        return user;
    }

    public void setUser(ProfileDTO user) {
        this.user = user;
    }

    public HonorBadgeDTO getBadge() {
        return badge;
    }

    public void setBadge(HonorBadgeDTO badge) {
        this.badge = badge;
    }

    public ProfileDTO getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(ProfileDTO awardedBy) {
        this.awardedBy = awardedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserHonorBadgeDTO)) {
            return false;
        }

        UserHonorBadgeDTO userHonorBadgeDTO = (UserHonorBadgeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, userHonorBadgeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserHonorBadgeDTO{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", awardedAt='" + getAwardedAt() + "'" +
            ", user=" + getUser() +
            ", badge=" + getBadge() +
            ", awardedBy=" + getAwardedBy() +
            "}";
    }
}
