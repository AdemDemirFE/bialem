package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.NotificationDeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "notification_delivery_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationDeliveryLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private AppNotification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "push_device_id")
    private PushDeviceToken pushDevice;

    @NotNull
    @Size(max = 50)
    @Column(name = "provider", length = 50, nullable = false)
    private String provider;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 40, nullable = false)
    private NotificationDeliveryStatus status;

    @Size(max = 200)
    @Column(name = "provider_message_id", length = 200)
    private String providerMessageId;

    @Size(max = 120)
    @Column(name = "error_code", length = 120)
    private String errorCode;

    @Size(max = 2000)
    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @Column(name = "attempt_number")
    private Integer attemptNumber;

    @Column(name = "sent_at")
    private Instant sentAt;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

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

    public PushDeviceToken getPushDevice() {
        return this.pushDevice;
    }

    public void setPushDevice(PushDeviceToken pushDevice) {
        this.pushDevice = pushDevice;
    }

    public String getProvider() {
        return this.provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public NotificationDeliveryStatus getStatus() {
        return this.status;
    }

    public void setStatus(NotificationDeliveryStatus status) {
        this.status = status;
    }

    public String getProviderMessageId() {
        return this.providerMessageId;
    }

    public void setProviderMessageId(String providerMessageId) {
        this.providerMessageId = providerMessageId;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getAttemptNumber() {
        return this.attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public Instant getSentAt() {
        return this.sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDeliveryLog)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationDeliveryLog) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "NotificationDeliveryLog{" +
            "id=" + getId() +
            ", provider='" + getProvider() + "'" +
            ", status='" + getStatus() + "'" +
            ", notificationId='" + (getNotification() != null ? getNotification().getId() : null) + "'" +
            "}";
    }
}
