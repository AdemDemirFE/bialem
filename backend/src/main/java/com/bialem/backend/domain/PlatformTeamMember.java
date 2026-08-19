package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PlatformTeamRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PlatformTeamMember.
 */
@Entity
@Table(name = "platform_team_member")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlatformTeamMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_code", nullable = false)
    private PlatformTeamRole roleCode;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile assignedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlatformTeamMember id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlatformTeamRole getRoleCode() {
        return this.roleCode;
    }

    public PlatformTeamMember roleCode(PlatformTeamRole roleCode) {
        this.setRoleCode(roleCode);
        return this;
    }

    public void setRoleCode(PlatformTeamRole roleCode) {
        this.roleCode = roleCode;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PlatformTeamMember createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PlatformTeamMember updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Profile getUser() {
        return this.user;
    }

    public void setUser(Profile profile) {
        this.user = profile;
    }

    public PlatformTeamMember user(Profile profile) {
        this.setUser(profile);
        return this;
    }

    public Profile getAssignedBy() {
        return this.assignedBy;
    }

    public void setAssignedBy(Profile profile) {
        this.assignedBy = profile;
    }

    public PlatformTeamMember assignedBy(Profile profile) {
        this.setAssignedBy(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatformTeamMember)) {
            return false;
        }
        return getId() != null && getId().equals(((PlatformTeamMember) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlatformTeamMember{" +
            "id=" + getId() +
            ", roleCode='" + getRoleCode() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
