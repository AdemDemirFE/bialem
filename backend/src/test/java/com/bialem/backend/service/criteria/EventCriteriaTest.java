package com.bialem.backend.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EventCriteriaTest {

    @Test
    void newEventCriteriaHasAllFiltersNullTest() {
        var eventCriteria = new EventCriteria();
        assertThat(eventCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void eventCriteriaFluentMethodsCreatesFiltersTest() {
        var eventCriteria = new EventCriteria();

        setAllFilters(eventCriteria);

        assertThat(eventCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void eventCriteriaCopyCreatesNullFilterTest() {
        var eventCriteria = new EventCriteria();
        var copy = eventCriteria.copy();

        assertThat(eventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(eventCriteria)
        );
    }

    @Test
    void eventCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var eventCriteria = new EventCriteria();
        setAllFilters(eventCriteria);

        var copy = eventCriteria.copy();

        assertThat(eventCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(eventCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var eventCriteria = new EventCriteria();

        assertThat(eventCriteria).hasToString("EventCriteria{}");
    }

    private static void setAllFilters(EventCriteria eventCriteria) {
        eventCriteria.id();
        eventCriteria.title();
        eventCriteria.startsAt();
        eventCriteria.endsAt();
        eventCriteria.locationName();
        eventCriteria.addressText();
        eventCriteria.latitude();
        eventCriteria.longitude();
        eventCriteria.coverImageUrl();
        eventCriteria.capacity();
        eventCriteria.status();
        eventCriteria.rejectionReason();
        eventCriteria.publishedAt();
        eventCriteria.publishedToDiscovery();
        eventCriteria.groupModerationStatus();
        eventCriteria.platformModerationStatus();
        eventCriteria.cancelledAt();
        eventCriteria.cancellationReason();
        eventCriteria.createdAt();
        eventCriteria.updatedAt();
        eventCriteria.communityId();
        eventCriteria.categoryId();
        eventCriteria.createdById();
        eventCriteria.cancelledById();
        eventCriteria.participantsId();
        eventCriteria.messagesId();
        eventCriteria.ratingsId();
        eventCriteria.postsId();
        eventCriteria.distinct();
    }

    private static Condition<EventCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getStartsAt()) &&
                condition.apply(criteria.getEndsAt()) &&
                condition.apply(criteria.getLocationName()) &&
                condition.apply(criteria.getAddressText()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getCoverImageUrl()) &&
                condition.apply(criteria.getCapacity()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRejectionReason()) &&
                condition.apply(criteria.getPublishedAt()) &&
                condition.apply(criteria.getPublishedToDiscovery()) &&
                condition.apply(criteria.getGroupModerationStatus()) &&
                condition.apply(criteria.getPlatformModerationStatus()) &&
                condition.apply(criteria.getCancelledAt()) &&
                condition.apply(criteria.getCancellationReason()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getCommunityId()) &&
                condition.apply(criteria.getCategoryId()) &&
                condition.apply(criteria.getCreatedById()) &&
                condition.apply(criteria.getCancelledById()) &&
                condition.apply(criteria.getParticipantsId()) &&
                condition.apply(criteria.getMessagesId()) &&
                condition.apply(criteria.getRatingsId()) &&
                condition.apply(criteria.getPostsId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EventCriteria> copyFiltersAre(EventCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getStartsAt(), copy.getStartsAt()) &&
                condition.apply(criteria.getEndsAt(), copy.getEndsAt()) &&
                condition.apply(criteria.getLocationName(), copy.getLocationName()) &&
                condition.apply(criteria.getAddressText(), copy.getAddressText()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getCoverImageUrl(), copy.getCoverImageUrl()) &&
                condition.apply(criteria.getCapacity(), copy.getCapacity()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRejectionReason(), copy.getRejectionReason()) &&
                condition.apply(criteria.getPublishedAt(), copy.getPublishedAt()) &&
                condition.apply(criteria.getPublishedToDiscovery(), copy.getPublishedToDiscovery()) &&
                condition.apply(criteria.getGroupModerationStatus(), copy.getGroupModerationStatus()) &&
                condition.apply(criteria.getPlatformModerationStatus(), copy.getPlatformModerationStatus()) &&
                condition.apply(criteria.getCancelledAt(), copy.getCancelledAt()) &&
                condition.apply(criteria.getCancellationReason(), copy.getCancellationReason()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getCommunityId(), copy.getCommunityId()) &&
                condition.apply(criteria.getCategoryId(), copy.getCategoryId()) &&
                condition.apply(criteria.getCreatedById(), copy.getCreatedById()) &&
                condition.apply(criteria.getCancelledById(), copy.getCancelledById()) &&
                condition.apply(criteria.getParticipantsId(), copy.getParticipantsId()) &&
                condition.apply(criteria.getMessagesId(), copy.getMessagesId()) &&
                condition.apply(criteria.getRatingsId(), copy.getRatingsId()) &&
                condition.apply(criteria.getPostsId(), copy.getPostsId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
