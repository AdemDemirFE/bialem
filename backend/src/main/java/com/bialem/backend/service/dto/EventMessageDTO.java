package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.ModerationStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.EventMessage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventMessageDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 1, max = 1000)
    private String body;

    @NotNull
    private ModerationStatus moderationStatus;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private EventDTO event;

    private ProfileDTO author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
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

    public EventDTO getEvent() {
        return event;
    }

    public void setEvent(EventDTO event) {
        this.event = event;
    }

    public ProfileDTO getAuthor() {
        return author;
    }

    public void setAuthor(ProfileDTO author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventMessageDTO)) {
            return false;
        }

        EventMessageDTO eventMessageDTO = (EventMessageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventMessageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventMessageDTO{" +
            "id=" + getId() +
            ", body='" + getBody() + "'" +
            ", moderationStatus='" + getModerationStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", event=" + getEvent() +
            ", author=" + getAuthor() +
            "}";
    }
}
