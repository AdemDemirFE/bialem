package com.bialem.backend.service;

import com.bialem.backend.domain.*; // for static metamodels
import com.bialem.backend.domain.Event;
import com.bialem.backend.repository.EventRepository;
import com.bialem.backend.service.criteria.EventCriteria;
import com.bialem.backend.service.dto.EventDTO;
import com.bialem.backend.service.mapper.EventMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Event} entities in the database.
 * The main input is a {@link EventCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EventDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EventQueryService extends QueryService<Event> {

    private static final Logger LOG = LoggerFactory.getLogger(EventQueryService.class);

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    public EventQueryService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    /**
     * Return a {@link Page} of {@link EventDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> findByCriteria(EventCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.findAll(specification, page).map(eventMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EventCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Event> specification = createSpecification(criteria);
        return eventRepository.count(specification);
    }

    /**
     * Function to convert {@link EventCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Event> createSpecification(EventCriteria criteria) {
        Specification<Event> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Event_.id),
                buildStringSpecification(criteria.getTitle(), Event_.title),
                buildRangeSpecification(criteria.getStartsAt(), Event_.startsAt),
                buildRangeSpecification(criteria.getEndsAt(), Event_.endsAt),
                buildStringSpecification(criteria.getLocationName(), Event_.locationName),
                buildStringSpecification(criteria.getAddressText(), Event_.addressText),
                buildRangeSpecification(criteria.getLatitude(), Event_.latitude),
                buildRangeSpecification(criteria.getLongitude(), Event_.longitude),
                buildStringSpecification(criteria.getCoverImageUrl(), Event_.coverImageUrl),
                buildRangeSpecification(criteria.getCapacity(), Event_.capacity),
                buildSpecification(criteria.getStatus(), Event_.status),
                buildStringSpecification(criteria.getRejectionReason(), Event_.rejectionReason),
                buildRangeSpecification(criteria.getPublishedAt(), Event_.publishedAt),
                buildSpecification(criteria.getPublishedToDiscovery(), Event_.publishedToDiscovery),
                buildSpecification(criteria.getGroupModerationStatus(), Event_.groupModerationStatus),
                buildSpecification(criteria.getPlatformModerationStatus(), Event_.platformModerationStatus),
                buildRangeSpecification(criteria.getCancelledAt(), Event_.cancelledAt),
                buildStringSpecification(criteria.getCancellationReason(), Event_.cancellationReason),
                buildRangeSpecification(criteria.getCreatedAt(), Event_.createdAt),
                buildRangeSpecification(criteria.getUpdatedAt(), Event_.updatedAt),
                buildSpecification(criteria.getCommunityId(), root -> root.join(Event_.community, JoinType.LEFT).get(Community_.id)),
                buildSpecification(criteria.getCategoryId(), root -> root.join(Event_.category, JoinType.LEFT).get(Community_.id)),
                buildSpecification(criteria.getCreatedById(), root -> root.join(Event_.createdBy, JoinType.LEFT).get(Profile_.id)),
                buildSpecification(criteria.getCancelledById(), root -> root.join(Event_.cancelledBy, JoinType.LEFT).get(Profile_.id)),
                buildSpecification(criteria.getParticipantsId(), root ->
                    root.join(Event_.participants, JoinType.LEFT).get(EventParticipant_.id)
                ),
                buildSpecification(criteria.getMessagesId(), root -> root.join(Event_.messages, JoinType.LEFT).get(EventMessage_.id)),
                buildSpecification(criteria.getRatingsId(), root -> root.join(Event_.ratings, JoinType.LEFT).get(EventRating_.id)),
                buildSpecification(criteria.getPostsId(), root -> root.join(Event_.posts, JoinType.LEFT).get(Post_.id))
            );
        }
        return specification;
    }
}
