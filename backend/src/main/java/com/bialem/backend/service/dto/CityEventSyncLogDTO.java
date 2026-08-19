package com.bialem.backend.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.CityEventSyncLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEventSyncLogDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String providerCode;

    @NotNull
    @Size(max = 20)
    private String status;

    @NotNull
    private Integer importedCount;

    @Lob
    private String errorMessage;

    @NotNull
    private Instant startedAt;

    @NotNull
    private Instant finishedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(Integer importedCount) {
        this.importedCount = importedCount;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Instant finishedAt) {
        this.finishedAt = finishedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityEventSyncLogDTO)) {
            return false;
        }

        CityEventSyncLogDTO cityEventSyncLogDTO = (CityEventSyncLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cityEventSyncLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEventSyncLogDTO{" +
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
