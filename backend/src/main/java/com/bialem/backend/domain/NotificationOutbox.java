package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "notification_outbox")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationOutbox implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private AppNotification notification;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 40, nullable = false)
    private NotificationOutboxStatus status = NotificationOutboxStatus.PENDING;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @NotNull
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount = 0;

    @NotNull
    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 5;

    @Column(name = "next_attempt_at")
    private Instant nextAttemptAt;

    @Size(max = 2000)
    @Column(name = "last_error", length = 2000)
    private String lastError;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Size(max = 500)
    @Column(name = "idempotency_key", length = 500, unique = true)
    private String idempotencyKey;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppNotification getNotification() {
        return this.notification;
    }

    public void setNotification(AppNotification notification) {
        this.notification = notification;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NotificationOutboxStatus getStatus() {
        return this.status;
    }

    public void setStatus(NotificationOutboxStatus status) {
        this.status = status;
    }

    public Instant getScheduledAt() {
        return this.scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public Integer getAttemptCount() {
        return this.attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Integer getMaxAttempts() {
        return this.maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Instant getNextAttemptAt() {
        return this.nextAttemptAt;
    }

    public void setNextAttemptAt(Instant nextAttemptAt) {
        this.nextAttemptAt = nextAttemptAt;
    }

    public String getLastError() {
        return this.lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getProcessedAt() {
        return this.processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationOutbox)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationOutbox) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "NotificationOutbox{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", attemptCount='" + getAttemptCount() + "'" +
            ", userId='" + (getUser() != null ? getUser().getId() : null) + "'" +
            "}";
    }
}
