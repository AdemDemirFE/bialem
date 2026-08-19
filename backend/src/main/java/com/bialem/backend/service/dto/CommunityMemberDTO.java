package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.CommunityMember} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityMemberDTO implements Serializable {

    private Long id;

    @NotNull
    private CommunityMemberRole role;

    @NotNull
    private CommunityMemberStatus status;

    @NotNull
    private Instant createdAt;

    private CommunityDTO community;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CommunityMemberRole getRole() {
        return role;
    }

    public void setRole(CommunityMemberRole role) {
        this.role = role;
    }

    public CommunityMemberStatus getStatus() {
        return status;
    }

    public void setStatus(CommunityMemberStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public CommunityDTO getCommunity() {
        return community;
    }

    public void setCommunity(CommunityDTO community) {
        this.community = community;
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
        if (!(o instanceof CommunityMemberDTO)) {
            return false;
        }

        CommunityMemberDTO communityMemberDTO = (CommunityMemberDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, communityMemberDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityMemberDTO{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", community=" + getCommunity() +
            ", user=" + getUser() +
            "}";
    }
}
