package com.bialem.backend.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.CityEventInterest} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEventInterestDTO implements Serializable {

    private Long id;

    @NotNull
    private Boolean lookingForCompany;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private CityEventDTO cityEvent;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLookingForCompany() {
        return lookingForCompany;
    }

    public void setLookingForCompany(Boolean lookingForCompany) {
        this.lookingForCompany = lookingForCompany;
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

    public CityEventDTO getCityEvent() {
        return cityEvent;
    }

    public void setCityEvent(CityEventDTO cityEvent) {
        this.cityEvent = cityEvent;
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
        if (!(o instanceof CityEventInterestDTO)) {
            return false;
        }

        CityEventInterestDTO cityEventInterestDTO = (CityEventInterestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cityEventInterestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEventInterestDTO{" +
            "id=" + getId() +
            ", lookingForCompany='" + getLookingForCompany() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", cityEvent=" + getCityEvent() +
            ", user=" + getUser() +
            "}";
    }
}
