package com.bialem.backend.service.criteria;

import com.bialem.backend.domain.enumeration.ReportStatus;
import com.bialem.backend.domain.enumeration.ReportTargetType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.bialem.backend.domain.Report} entity. This class is used
 * in {@link com.bialem.backend.web.rest.ReportResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /reports?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReportCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReportTargetType
     */
    public static class ReportTargetTypeFilter extends Filter<ReportTargetType> {

        public ReportTargetTypeFilter() {}

        public ReportTargetTypeFilter(ReportTargetTypeFilter filter) {
            super(filter);
        }

        @Override
        public ReportTargetTypeFilter copy() {
            return new ReportTargetTypeFilter(this);
        }
    }

    /**
     * Class for filtering ReportStatus
     */
    public static class ReportStatusFilter extends Filter<ReportStatus> {

        public ReportStatusFilter() {}

        public ReportStatusFilter(ReportStatusFilter filter) {
            super(filter);
        }

        @Override
        public ReportStatusFilter copy() {
            return new ReportStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private ReportTargetTypeFilter targetType;

    private StringFilter targetId;

    private StringFilter reason;

    private ReportStatusFilter status;

    private InstantFilter resolvedAt;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter reporterId;

    private LongFilter resolvedById;

    private Boolean distinct;

    public ReportCriteria() {}

    public ReportCriteria(ReportCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.targetType = other.optionalTargetType().map(ReportTargetTypeFilter::copy).orElse(null);
        this.targetId = other.optionalTargetId().map(StringFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ReportStatusFilter::copy).orElse(null);
        this.resolvedAt = other.optionalResolvedAt().map(InstantFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.reporterId = other.optionalReporterId().map(LongFilter::copy).orElse(null);
        this.resolvedById = other.optionalResolvedById().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReportCriteria copy() {
        return new ReportCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ReportTargetTypeFilter getTargetType() {
        return targetType;
    }

    public Optional<ReportTargetTypeFilter> optionalTargetType() {
        return Optional.ofNullable(targetType);
    }

    public ReportTargetTypeFilter targetType() {
        if (targetType == null) {
            setTargetType(new ReportTargetTypeFilter());
        }
        return targetType;
    }

    public void setTargetType(ReportTargetTypeFilter targetType) {
        this.targetType = targetType;
    }

    public StringFilter getTargetId() {
        return targetId;
    }

    public Optional<StringFilter> optionalTargetId() {
        return Optional.ofNullable(targetId);
    }

    public StringFilter targetId() {
        if (targetId == null) {
            setTargetId(new StringFilter());
        }
        return targetId;
    }

    public void setTargetId(StringFilter targetId) {
        this.targetId = targetId;
    }

    public StringFilter getReason() {
        return reason;
    }

    public Optional<StringFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public StringFilter reason() {
        if (reason == null) {
            setReason(new StringFilter());
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public ReportStatusFilter getStatus() {
        return status;
    }

    public Optional<ReportStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ReportStatusFilter status() {
        if (status == null) {
            setStatus(new ReportStatusFilter());
        }
        return status;
    }

    public void setStatus(ReportStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getResolvedAt() {
        return resolvedAt;
    }

    public Optional<InstantFilter> optionalResolvedAt() {
        return Optional.ofNullable(resolvedAt);
    }

    public InstantFilter resolvedAt() {
        if (resolvedAt == null) {
            setResolvedAt(new InstantFilter());
        }
        return resolvedAt;
    }

    public void setResolvedAt(InstantFilter resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getReporterId() {
        return reporterId;
    }

    public Optional<LongFilter> optionalReporterId() {
        return Optional.ofNullable(reporterId);
    }

    public LongFilter reporterId() {
        if (reporterId == null) {
            setReporterId(new LongFilter());
        }
        return reporterId;
    }

    public void setReporterId(LongFilter reporterId) {
        this.reporterId = reporterId;
    }

    public LongFilter getResolvedById() {
        return resolvedById;
    }

    public Optional<LongFilter> optionalResolvedById() {
        return Optional.ofNullable(resolvedById);
    }

    public LongFilter resolvedById() {
        if (resolvedById == null) {
            setResolvedById(new LongFilter());
        }
        return resolvedById;
    }

    public void setResolvedById(LongFilter resolvedById) {
        this.resolvedById = resolvedById;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReportCriteria that = (ReportCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(targetType, that.targetType) &&
            Objects.equals(targetId, that.targetId) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(status, that.status) &&
            Objects.equals(resolvedAt, that.resolvedAt) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(reporterId, that.reporterId) &&
            Objects.equals(resolvedById, that.resolvedById) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targetType, targetId, reason, status, resolvedAt, createdAt, updatedAt, reporterId, resolvedById, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReportCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTargetType().map(f -> "targetType=" + f + ", ").orElse("") +
            optionalTargetId().map(f -> "targetId=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalResolvedAt().map(f -> "resolvedAt=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalReporterId().map(f -> "reporterId=" + f + ", ").orElse("") +
            optionalResolvedById().map(f -> "resolvedById=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
