package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.domain.enumeration.NotificationPriority;
import com.bialem.backend.domain.enumeration.NotificationScheduleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "notification_template")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "code", length = 80, nullable = false, unique = true)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 80, nullable = false, unique = true)
    private NotificationEventType eventType;

    @NotNull
    @Size(max = 120)
    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @NotNull
    @Size(max = 200)
    @Column(name = "title_template", length = 200, nullable = false)
    private String titleTemplate;

    @Size(max = 2000)
    @Column(name = "body_template", length = 2000)
    private String bodyTemplate;

    @Size(max = 500)
    @Column(name = "route_template", length = 500)
    private String routeTemplate;

    @NotNull
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @NotNull
    @Column(name = "in_app_enabled", nullable = false)
    private Boolean inAppEnabled = true;

    @NotNull
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled = true;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20, nullable = false)
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Size(max = 80)
    @Column(name = "target_strategy", length = 80)
    private String targetStrategy;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", length = 40, nullable = false)
    private NotificationScheduleType scheduleType = NotificationScheduleType.IMMEDIATE;

    @Column(name = "delay_minutes")
    private Integer delayMinutes;

    @Size(max = 10)
    @Column(name = "preferred_send_time", length = 10)
    private String preferredSendTime;

    @Size(max = 80)
    @Column(name = "timezone", length = 80)
    private String timezone = "Europe/Istanbul";

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Size(max = 80)
    @Column(name = "created_by", length = 80)
    private String createdBy;

    @Size(max = 80)
    @Column(name = "updated_by", length = 80)
    private String updatedBy;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public NotificationEventType getEventType() {
        return this.eventType;
    }

    public void setEventType(NotificationEventType eventType) {
        this.eventType = eventType;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleTemplate() {
        return this.titleTemplate;
    }

    public void setTitleTemplate(String titleTemplate) {
        this.titleTemplate = titleTemplate;
    }

    public String getBodyTemplate() {
        return this.bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public String getRouteTemplate() {
        return this.routeTemplate;
    }

    public void setRouteTemplate(String routeTemplate) {
        this.routeTemplate = routeTemplate;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public NotificationPriority getPriority() {
        return this.priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public String getTargetStrategy() {
        return this.targetStrategy;
    }

    public void setTargetStrategy(String targetStrategy) {
        this.targetStrategy = targetStrategy;
    }

    public NotificationScheduleType getScheduleType() {
        return this.scheduleType;
    }

    public void setScheduleType(NotificationScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getDelayMinutes() {
        return this.delayMinutes;
    }

    public void setDelayMinutes(Integer delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public String getPreferredSendTime() {
        return this.preferredSendTime;
    }

    public void setPreferredSendTime(String preferredSendTime) {
        this.preferredSendTime = preferredSendTime;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
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

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationTemplate)) {
            return false;
        }
        return getId() != null && getId().equals(((NotificationTemplate) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "NotificationTemplate{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", eventType='" + getEventType() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }
}
