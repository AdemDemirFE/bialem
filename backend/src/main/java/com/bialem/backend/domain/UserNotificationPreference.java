package com.bialem.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "user_notification_preference")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserNotificationPreference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Size(max = 80)
    @Column(name = "notification_type", length = 80, nullable = false)
    private String notificationType;

    @NotNull
    @Column(name = "in_app_enabled", nullable = false)
    private Boolean inAppEnabled = true;

    @NotNull
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled = true;

    @Column(name = "email_enabled")
    private Boolean emailEnabled = false;

    @Column(name = "muted_until")
    private Instant mutedUntil;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "mandatory")
    private Boolean mandatory = false;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public Boolean getInAppEnabled() {
        return this.inAppEnabled;
    }

    public void setInAppEnabled(Boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }

    public Boolean getPushEnabled() {
        return this.pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public Boolean getEmailEnabled() {
        return this.emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Instant getMutedUntil() {
        return this.mutedUntil;
    }

    public void setMutedUntil(Instant mutedUntil) {
        this.mutedUntil = mutedUntil;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getMandatory() {
        return this.mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserNotificationPreference)) {
            return false;
        }
        return getId() != null && getId().equals(((UserNotificationPreference) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserNotificationPreference{" +
            "id=" + getId() +
            ", notificationType='" + getNotificationType() + "'" +
            ", inAppEnabled='" + getInAppEnabled() + "'" +
            ", pushEnabled='" + getPushEnabled() + "'" +
            "}";
    }
}
