package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.ReportStatus;
import com.bialem.backend.domain.enumeration.ReportTargetType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Report.
 */
@Entity
@Table(name = "report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReportTargetType targetType;

    @NotNull
    @Column(name = "target_id", nullable = false)
    private String targetId;

    @NotNull
    @Size(max = 500)
    @Column(name = "reason", length = 500, nullable = false)
    private String reason;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "details")
    private String details;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile resolvedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Report id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportTargetType getTargetType() {
        return this.targetType;
    }

    public Report targetType(ReportTargetType targetType) {
        this.setTargetType(targetType);
        return this;
    }

    public void setTargetType(ReportTargetType targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public Report targetId(String targetId) {
        this.setTargetId(targetId);
        return this;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getReason() {
        return this.reason;
    }

    public Report reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return this.details;
    }

    public Report details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ReportStatus getStatus() {
        return this.status;
    }

    public Report status(ReportStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Instant getResolvedAt() {
        return this.resolvedAt;
    }

    public Report resolvedAt(Instant resolvedAt) {
        this.setResolvedAt(resolvedAt);
        return this;
    }

    public void setResolvedAt(Instant resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Report createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Report updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Profile getReporter() {
        return this.reporter;
    }

    public void setReporter(Profile profile) {
        this.reporter = profile;
    }

    public Report reporter(Profile profile) {
        this.setReporter(profile);
        return this;
    }

    public Profile getResolvedBy() {
        return this.resolvedBy;
    }

    public void setResolvedBy(Profile profile) {
        this.resolvedBy = profile;
    }

    public Report resolvedBy(Profile profile) {
        this.setResolvedBy(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Report)) {
            return false;
        }
        return getId() != null && getId().equals(((Report) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Report{" +
            "id=" + getId() +
            ", targetType='" + getTargetType() + "'" +
            ", targetId='" + getTargetId() + "'" +
            ", reason='" + getReason() + "'" +
            ", details='" + getDetails() + "'" +
            ", status='" + getStatus() + "'" +
            ", resolvedAt='" + getResolvedAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
