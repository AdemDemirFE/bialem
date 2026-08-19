package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.PlatformTeamRole;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PlatformTeamMember} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlatformTeamMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private PlatformTeamRole roleCode;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private ProfileDTO user;

    private ProfileDTO assignedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlatformTeamRole getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(PlatformTeamRole roleCode) {
        this.roleCode = roleCode;
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

    public ProfileDTO getUser() {
        return user;
    }

    public void setUser(ProfileDTO user) {
        this.user = user;
    }

    public ProfileDTO getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(ProfileDTO assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatformTeamMemberDTO)) {
            return false;
        }

        PlatformTeamMemberDTO platformTeamMemberDTO = (PlatformTeamMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, platformTeamMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlatformTeamMemberDTO{" +
            "id=" + getId() +
            ", roleCode='" + getRoleCode() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", user=" + getUser() +
            ", assignedBy=" + getAssignedBy() +
            "}";
    }
}
