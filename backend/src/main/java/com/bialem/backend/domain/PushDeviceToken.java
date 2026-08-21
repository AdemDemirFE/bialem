package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PushPlatform;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PushDeviceToken.
 */
@Entity
@Table(name = "push_device_token")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PushDeviceToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 512)
    @Column(name = "token", length = 512, nullable = false, unique = true)
    private String token;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private PushPlatform platform;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "active")
    private Boolean active = true;

    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "last_success_at")
    private Instant lastSuccessAt;

    @Column(name = "last_failure_at")
    private Instant lastFailureAt;

    @Size(max = 255)
    @Column(name = "firebase_installation_id", length = 255)
    private String firebaseInstallationId;

    @Size(max = 255)
    @Column(name = "device_uuid", length = 255)
    private String deviceUuid;

    @Size(max = 80)
    @Column(name = "app_version", length = 80)
    private String appVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private User user;

    public Long getId() {
        return this.id;
    }

    public PushDeviceToken id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public PushDeviceToken token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PushPlatform getPlatform() {
        return this.platform;
    }

    public PushDeviceToken platform(PushPlatform platform) {
        this.setPlatform(platform);
        return this;
    }

    public void setPlatform(PushPlatform platform) {
        this.platform = platform;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PushDeviceToken createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PushDeviceToken updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PushDeviceToken user(User user) {
        this.setUser(user);
        return this;
    }

    public Boolean getActive() {
        return this.active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PushDeviceToken active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public Boolean getNotificationsEnabled() {
        return this.notificationsEnabled;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public PushDeviceToken notificationsEnabled(Boolean notificationsEnabled) {
        this.setNotificationsEnabled(notificationsEnabled);
        return this;
    }

    public Instant getLastSeenAt() {
        return this.lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public PushDeviceToken lastSeenAt(Instant lastSeenAt) {
        this.setLastSeenAt(lastSeenAt);
        return this;
    }

    public Instant getLastSuccessAt() {
        return this.lastSuccessAt;
    }

    public void setLastSuccessAt(Instant lastSuccessAt) {
        this.lastSuccessAt = lastSuccessAt;
    }

    public PushDeviceToken lastSuccessAt(Instant lastSuccessAt) {
        this.setLastSuccessAt(lastSuccessAt);
        return this;
    }

    public Instant getLastFailureAt() {
        return this.lastFailureAt;
    }

    public void setLastFailureAt(Instant lastFailureAt) {
        this.lastFailureAt = lastFailureAt;
    }

    public PushDeviceToken lastFailureAt(Instant lastFailureAt) {
        this.setLastFailureAt(lastFailureAt);
        return this;
    }

    public String getFirebaseInstallationId() {
        return this.firebaseInstallationId;
    }

    public void setFirebaseInstallationId(String firebaseInstallationId) {
        this.firebaseInstallationId = firebaseInstallationId;
    }

    public PushDeviceToken firebaseInstallationId(String firebaseInstallationId) {
        this.setFirebaseInstallationId(firebaseInstallationId);
        return this;
    }

    public String getDeviceUuid() {
        return this.deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }

    public PushDeviceToken deviceUuid(String deviceUuid) {
        this.setDeviceUuid(deviceUuid);
        return this;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public PushDeviceToken appVersion(String appVersion) {
        this.setAppVersion(appVersion);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PushDeviceToken)) {
            return false;
        }
        return getId() != null && getId().equals(((PushDeviceToken) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PushDeviceToken{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", platform='" + getPlatform() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
