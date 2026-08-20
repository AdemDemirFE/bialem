package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.PushPlatform;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PushDeviceToken} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PushDeviceTokenDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 512)
    private String token;

    @NotNull
    private PushPlatform platform;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PushPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(PushPlatform platform) {
        this.platform = platform;
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PushDeviceTokenDTO)) {
            return false;
        }

        PushDeviceTokenDTO pushDeviceTokenDTO = (PushDeviceTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pushDeviceTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PushDeviceTokenDTO{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", platform='" + getPlatform() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
