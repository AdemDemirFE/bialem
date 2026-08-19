package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.PushPlatform;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.PushToken} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PushTokenDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 512)
    private String deviceToken;

    @NotNull
    private PushPlatform platform;

    @Size(max = 120)
    private String deviceName;

    @NotNull
    private Boolean isActive;

    @NotNull
    private Instant lastSeenAt;

    @NotNull
    private Instant createdAt;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public PushPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(PushPlatform platform) {
        this.platform = platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
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
        if (!(o instanceof PushTokenDTO)) {
            return false;
        }

        PushTokenDTO pushTokenDTO = (PushTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pushTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PushTokenDTO{" +
            "id=" + getId() +
            ", deviceToken='" + getDeviceToken() + "'" +
            ", platform='" + getPlatform() + "'" +
            ", deviceName='" + getDeviceName() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", lastSeenAt='" + getLastSeenAt() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
