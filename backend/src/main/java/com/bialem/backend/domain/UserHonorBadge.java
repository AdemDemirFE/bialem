package com.bialem.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A UserHonorBadge.
 */
@Entity
@Table(name = "user_honor_badge")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserHonorBadge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 500)
    @Column(name = "reason", length = 500)
    private String reason;

    @NotNull
    @Column(name = "awarded_at", nullable = false)
    private Instant awardedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "community" }, allowSetters = true)
    private HonorBadge badge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile awardedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public UserHonorBadge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReason() {
        return this.reason;
    }

    public UserHonorBadge reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getAwardedAt() {
        return this.awardedAt;
    }

    public UserHonorBadge awardedAt(Instant awardedAt) {
        this.setAwardedAt(awardedAt);
        return this;
    }

    public void setAwardedAt(Instant awardedAt) {
        this.awardedAt = awardedAt;
    }

    public Profile getUser() {
        return this.user;
    }

    public void setUser(Profile profile) {
        this.user = profile;
    }

    public UserHonorBadge user(Profile profile) {
        this.setUser(profile);
        return this;
    }

    public HonorBadge getBadge() {
        return this.badge;
    }

    public void setBadge(HonorBadge honorBadge) {
        this.badge = honorBadge;
    }

    public UserHonorBadge badge(HonorBadge honorBadge) {
        this.setBadge(honorBadge);
        return this;
    }

    public Profile getAwardedBy() {
        return this.awardedBy;
    }

    public void setAwardedBy(Profile profile) {
        this.awardedBy = profile;
    }

    public UserHonorBadge awardedBy(Profile profile) {
        this.setAwardedBy(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserHonorBadge)) {
            return false;
        }
        return getId() != null && getId().equals(((UserHonorBadge) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserHonorBadge{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", awardedAt='" + getAwardedAt() + "'" +
            "}";
    }
}
