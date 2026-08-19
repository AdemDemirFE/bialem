package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.HonorBadgeType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HonorBadge.
 */
@Entity
@Table(name = "honor_badge")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HonorBadge implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "code", length = 80, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 160)
    @Column(name = "name_template", length = 160, nullable = false)
    private String nameTemplate;

    @NotNull
    @Size(max = 500)
    @Column(name = "description", length = 500, nullable = false)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private HonorBadgeType badgeType;

    @NotNull
    @Min(value = 1)
    @Column(name = "minimum_check_ins", nullable = false)
    private Integer minimumCheckIns;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "parent",
            "categoryHub",
            "createdBy",
            "leadModerator",
            "children",
            "categorizedGroups",
            "members",
            "assistants",
            "events",
            "posts",
            "storyTargets",
        },
        allowSetters = true
    )
    private Community community;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HonorBadge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public HonorBadge code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameTemplate() {
        return this.nameTemplate;
    }

    public HonorBadge nameTemplate(String nameTemplate) {
        this.setNameTemplate(nameTemplate);
        return this;
    }

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    public String getDescription() {
        return this.description;
    }

    public HonorBadge description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HonorBadgeType getBadgeType() {
        return this.badgeType;
    }

    public HonorBadge badgeType(HonorBadgeType badgeType) {
        this.setBadgeType(badgeType);
        return this;
    }

    public void setBadgeType(HonorBadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public Integer getMinimumCheckIns() {
        return this.minimumCheckIns;
    }

    public HonorBadge minimumCheckIns(Integer minimumCheckIns) {
        this.setMinimumCheckIns(minimumCheckIns);
        return this;
    }

    public void setMinimumCheckIns(Integer minimumCheckIns) {
        this.minimumCheckIns = minimumCheckIns;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public HonorBadge isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public HonorBadge createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Community getCommunity() {
        return this.community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public HonorBadge community(Community community) {
        this.setCommunity(community);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HonorBadge)) {
            return false;
        }
        return getId() != null && getId().equals(((HonorBadge) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HonorBadge{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", nameTemplate='" + getNameTemplate() + "'" +
            ", description='" + getDescription() + "'" +
            ", badgeType='" + getBadgeType() + "'" +
            ", minimumCheckIns=" + getMinimumCheckIns() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
