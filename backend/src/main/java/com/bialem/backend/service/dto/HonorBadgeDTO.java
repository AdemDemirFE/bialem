package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.HonorBadgeType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.HonorBadge} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HonorBadgeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 80)
    private String code;

    @NotNull
    @Size(max = 160)
    private String nameTemplate;

    @NotNull
    @Size(max = 500)
    private String description;

    @NotNull
    private HonorBadgeType badgeType;

    @NotNull
    @Min(value = 1)
    private Integer minimumCheckIns;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant createdAt;

    private CommunityDTO community;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameTemplate() {
        return nameTemplate;
    }

    public void setNameTemplate(String nameTemplate) {
        this.nameTemplate = nameTemplate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HonorBadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(HonorBadgeType badgeType) {
        this.badgeType = badgeType;
    }

    public Integer getMinimumCheckIns() {
        return minimumCheckIns;
    }

    public void setMinimumCheckIns(Integer minimumCheckIns) {
        this.minimumCheckIns = minimumCheckIns;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HonorBadgeDTO)) {
            return false;
        }

        HonorBadgeDTO honorBadgeDTO = (HonorBadgeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, honorBadgeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HonorBadgeDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", nameTemplate='" + getNameTemplate() + "'" +
            ", description='" + getDescription() + "'" +
            ", badgeType='" + getBadgeType() + "'" +
            ", minimumCheckIns=" + getMinimumCheckIns() +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", community=" + getCommunity() +
            "}";
    }
}
