package com.bialem.backend.service.dto;

import com.bialem.backend.domain.enumeration.EventParticipantStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bialem.backend.domain.EventParticipant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventParticipantDTO implements Serializable {

    private Long id;

    @NotNull
    private EventParticipantStatus status;

    @Size(max = 500)
    private String note;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    private EventDTO event;

    private ProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(EventParticipantStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        if (!(o instanceof EventParticipantDTO)) {
            return false;
        }

        EventParticipantDTO eventParticipantDTO = (EventParticipantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventParticipantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventParticipantDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", event=" + getEvent() +
            ", user=" + getUser() +
            "}";
    }
}
