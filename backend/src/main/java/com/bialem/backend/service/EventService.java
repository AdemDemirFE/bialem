package com.bialem.backend.service;

import com.bialem.backend.domain.Event;
import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.notification.NotificationEvent;
import com.bialem.backend.notification.NotificationEventPublisher;
import com.bialem.backend.repository.EventRepository;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.mapper.EventMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bialem.backend.domain.Event}.
 */
@Service
@Transactional
public class EventService {

    private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    private final NotificationEventPublisher notificationEventPublisher;

    public EventService(EventRepository eventRepository, EventMapper eventMapper, NotificationEventPublisher notificationEventPublisher) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.notificationEventPublisher = notificationEventPublisher;
    }

    /**
     * Save a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO save(EventDTO eventDTO) {
        LOG.debug("Request to save Event : {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        if (eventDTO.getStatus() == EventStatus.PUBLISHED) {
            publishEventPublishedEvent(eventDTO);
        }
        return eventMapper.toDto(event);
    }

    /**
     * Update a event.
     *
     * @param eventDTO the entity to save.
     * @return the persisted entity.
     */
    public EventDTO update(EventDTO eventDTO) {
        LOG.debug("Request to update Event : {}", eventDTO);
        Event event = eventMapper.toEntity(eventDTO);
        event = eventRepository.save(event);
        if (eventDTO.getStatus() == EventStatus.PUBLISHED) {
            publishEventPublishedEvent(eventDTO);
        }
        return eventMapper.toDto(event);
    }

    /**
     * Partially update a event.
     *
     * @param eventDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EventDTO> partialUpdate(EventDTO eventDTO) {
        LOG.debug("Request to partially update Event : {}", eventDTO);

        return eventRepository
            .findById(eventDTO.getId())
            .map(existingEvent -> {
                EventStatus previousStatus = existingEvent.getStatus();
                eventMapper.partialUpdate(existingEvent, eventDTO);
                Event saved = eventRepository.save(existingEvent);
                if (previousStatus != EventStatus.PUBLISHED && saved.getStatus() == EventStatus.PUBLISHED) {
                    publishEventPublishedEvent(eventMapper.toDto(saved));
                }
                return saved;
            })
            .map(eventMapper::toDto);
    }

    private void publishEventPublishedEvent(EventDTO eventDTO) {
        Long ownerId = userIdOf(eventDTO.getCreatedBy());
        if (ownerId == null) {
            return;
        }
        String idempotencyKey = "EVENT_PUBLISHED:" + eventDTO.getId() + ":" + ownerId;
        Map<String, Object> variables = new HashMap<>();
        variables.put("recipientUserId", ownerId);
        variables.put("eventOwnerId", ownerId);
        variables.put("eventId", eventDTO.getId());
        variables.put("eventName", eventDTO.getTitle());
        variables.put("startsAt", eventDTO.getStartsAt() != null ? eventDTO.getStartsAt().toString() : null);
        notificationEventPublisher.publish(new NotificationEvent(NotificationEventType.EVENT_PUBLISHED, idempotencyKey, variables));
    }

    private Long userIdOf(ProfileDTO profile) {
        return profile != null && profile.getUser() != null ? profile.getUser().getId() : null;
    }

    /**
     * Get one event by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EventDTO> findOne(Long id) {
        LOG.debug("Request to get Event : {}", id);
        return eventRepository.findById(id).map(eventMapper::toDto);
    }

    /**
     * Delete the event by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Event : {}", id);
        eventRepository.deleteById(id);
    }
}
