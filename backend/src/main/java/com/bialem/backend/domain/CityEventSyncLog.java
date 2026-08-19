package com.bialem.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A CityEventSyncLog.
 */
@Entity
@Table(name = "city_event_sync_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEventSyncLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "provider_code", length = 50, nullable = false)
    private String providerCode;

    @NotNull
    @Size(max = 20)
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @NotNull
    @Column(name = "imported_count", nullable = false)
    private Integer importedCount;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "error_message")
    private String errorMessage;

    @NotNull
    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @NotNull
    @Column(name = "finished_at", nullable = false)
    private Instant finishedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CityEventSyncLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderCode() {
        return this.providerCode;
    }

    public CityEventSyncLog providerCode(String providerCode) {
        this.setProviderCode(providerCode);
        return this;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getStatus() {
        return this.status;
    }

    public CityEventSyncLog status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getImportedCount() {
        return this.importedCount;
    }

    public CityEventSyncLog importedCount(Integer importedCount) {
        this.setImportedCount(importedCount);
        return this;
    }

    public void setImportedCount(Integer importedCount) {
        this.importedCount = importedCount;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public CityEventSyncLog errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartedAt() {
        return this.startedAt;
    }

    public CityEventSyncLog startedAt(Instant startedAt) {
        this.setStartedAt(startedAt);
        return this;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return this.finishedAt;
    }

    public CityEventSyncLog finishedAt(Instant finishedAt) {
        this.setFinishedAt(finishedAt);
        return this;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityEventSyncLog)) {
            return false;
        }
        return getId() != null && getId().equals(((CityEventSyncLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEventSyncLog{" +
            "id=" + getId() +
            ", providerCode='" + getProviderCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", importedCount=" + getImportedCount() +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", startedAt='" + getStartedAt() + "'" +
            ", finishedAt='" + getFinishedAt() + "'" +
            "}";
    }
}
