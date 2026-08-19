package com.bialem.backend.service.criteria;

import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.GroupModerationStatus;
import com.bialem.backend.domain.enumeration.PlatformModerationStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.bialem.backend.domain.Event} entity. This class is used
 * in {@link com.bialem.backend.web.rest.EventResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /events?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EventStatus
     */
    public static class EventStatusFilter extends Filter<EventStatus> {

        public EventStatusFilter() {}

        public EventStatusFilter(EventStatusFilter filter) {
            super(filter);
        }

        @Override
        public EventStatusFilter copy() {
            return new EventStatusFilter(this);
        }
    }

    /**
     * Class for filtering GroupModerationStatus
     */
    public static class GroupModerationStatusFilter extends Filter<GroupModerationStatus> {

        public GroupModerationStatusFilter() {}

        public GroupModerationStatusFilter(GroupModerationStatusFilter filter) {
            super(filter);
        }

        @Override
        public GroupModerationStatusFilter copy() {
            return new GroupModerationStatusFilter(this);
        }
    }

    /**
     * Class for filtering PlatformModerationStatus
     */
    public static class PlatformModerationStatusFilter extends Filter<PlatformModerationStatus> {

        public PlatformModerationStatusFilter() {}

        public PlatformModerationStatusFilter(PlatformModerationStatusFilter filter) {
            super(filter);
        }

        @Override
        public PlatformModerationStatusFilter copy() {
            return new PlatformModerationStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private InstantFilter startsAt;

    private InstantFilter endsAt;

    private StringFilter locationName;

    private StringFilter addressText;

    private BigDecimalFilter latitude;

    private BigDecimalFilter longitude;

    private StringFilter coverImageUrl;

    private IntegerFilter capacity;

    private EventStatusFilter status;

    private StringFilter rejectionReason;

    private InstantFilter publishedAt;

    private BooleanFilter publishedToDiscovery;

    private GroupModerationStatusFilter groupModerationStatus;

    private PlatformModerationStatusFilter platformModerationStatus;

    private InstantFilter cancelledAt;

    private StringFilter cancellationReason;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter communityId;

    private LongFilter categoryId;

    private LongFilter createdById;

    private LongFilter cancelledById;

    private LongFilter participantsId;

    private LongFilter messagesId;

    private LongFilter ratingsId;

    private LongFilter postsId;

    private Boolean distinct;

    public EventCriteria() {}

    public EventCriteria(EventCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.startsAt = other.optionalStartsAt().map(InstantFilter::copy).orElse(null);
        this.endsAt = other.optionalEndsAt().map(InstantFilter::copy).orElse(null);
        this.locationName = other.optionalLocationName().map(StringFilter::copy).orElse(null);
        this.addressText = other.optionalAddressText().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(BigDecimalFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(BigDecimalFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.capacity = other.optionalCapacity().map(IntegerFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EventStatusFilter::copy).orElse(null);
        this.rejectionReason = other.optionalRejectionReason().map(StringFilter::copy).orElse(null);
        this.publishedAt = other.optionalPublishedAt().map(InstantFilter::copy).orElse(null);
        this.publishedToDiscovery = other.optionalPublishedToDiscovery().map(BooleanFilter::copy).orElse(null);
        this.groupModerationStatus = other.optionalGroupModerationStatus().map(GroupModerationStatusFilter::copy).orElse(null);
        this.platformModerationStatus = other.optionalPlatformModerationStatus().map(PlatformModerationStatusFilter::copy).orElse(null);
        this.cancelledAt = other.optionalCancelledAt().map(InstantFilter::copy).orElse(null);
        this.cancellationReason = other.optionalCancellationReason().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.communityId = other.optionalCommunityId().map(LongFilter::copy).orElse(null);
        this.categoryId = other.optionalCategoryId().map(LongFilter::copy).orElse(null);
        this.createdById = other.optionalCreatedById().map(LongFilter::copy).orElse(null);
        this.cancelledById = other.optionalCancelledById().map(LongFilter::copy).orElse(null);
        this.participantsId = other.optionalParticipantsId().map(LongFilter::copy).orElse(null);
        this.messagesId = other.optionalMessagesId().map(LongFilter::copy).orElse(null);
        this.ratingsId = other.optionalRatingsId().map(LongFilter::copy).orElse(null);
        this.postsId = other.optionalPostsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EventCriteria copy() {
        return new EventCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public InstantFilter getStartsAt() {
        return startsAt;
    }

    public Optional<InstantFilter> optionalStartsAt() {
        return Optional.ofNullable(startsAt);
    }

    public InstantFilter startsAt() {
        if (startsAt == null) {
            setStartsAt(new InstantFilter());
        }
        return startsAt;
    }

    public void setStartsAt(InstantFilter startsAt) {
        this.startsAt = startsAt;
    }

    public InstantFilter getEndsAt() {
        return endsAt;
    }

    public Optional<InstantFilter> optionalEndsAt() {
        return Optional.ofNullable(endsAt);
    }

    public InstantFilter endsAt() {
        if (endsAt == null) {
            setEndsAt(new InstantFilter());
        }
        return endsAt;
    }

    public void setEndsAt(InstantFilter endsAt) {
        this.endsAt = endsAt;
    }

    public StringFilter getLocationName() {
        return locationName;
    }

    public Optional<StringFilter> optionalLocationName() {
        return Optional.ofNullable(locationName);
    }

    public StringFilter locationName() {
        if (locationName == null) {
            setLocationName(new StringFilter());
        }
        return locationName;
    }

    public void setLocationName(StringFilter locationName) {
        this.locationName = locationName;
    }

    public StringFilter getAddressText() {
        return addressText;
    }

    public Optional<StringFilter> optionalAddressText() {
        return Optional.ofNullable(addressText);
    }

    public StringFilter addressText() {
        if (addressText == null) {
            setAddressText(new StringFilter());
        }
        return addressText;
    }

    public void setAddressText(StringFilter addressText) {
        this.addressText = addressText;
    }

    public BigDecimalFilter getLatitude() {
        return latitude;
    }

    public Optional<BigDecimalFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public BigDecimalFilter latitude() {
        if (latitude == null) {
            setLatitude(new BigDecimalFilter());
        }
        return latitude;
    }

    public void setLatitude(BigDecimalFilter latitude) {
        this.latitude = latitude;
    }

    public BigDecimalFilter getLongitude() {
        return longitude;
    }

    public Optional<BigDecimalFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public BigDecimalFilter longitude() {
        if (longitude == null) {
            setLongitude(new BigDecimalFilter());
        }
        return longitude;
    }

    public void setLongitude(BigDecimalFilter longitude) {
        this.longitude = longitude;
    }

    public StringFilter getCoverImageUrl() {
        return coverImageUrl;
    }

    public Optional<StringFilter> optionalCoverImageUrl() {
        return Optional.ofNullable(coverImageUrl);
    }

    public StringFilter coverImageUrl() {
        if (coverImageUrl == null) {
            setCoverImageUrl(new StringFilter());
        }
        return coverImageUrl;
    }

    public void setCoverImageUrl(StringFilter coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public IntegerFilter getCapacity() {
        return capacity;
    }

    public Optional<IntegerFilter> optionalCapacity() {
        return Optional.ofNullable(capacity);
    }

    public IntegerFilter capacity() {
        if (capacity == null) {
            setCapacity(new IntegerFilter());
        }
        return capacity;
    }

    public void setCapacity(IntegerFilter capacity) {
        this.capacity = capacity;
    }

    public EventStatusFilter getStatus() {
        return status;
    }

    public Optional<EventStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EventStatusFilter status() {
        if (status == null) {
            setStatus(new EventStatusFilter());
        }
        return status;
    }

    public void setStatus(EventStatusFilter status) {
        this.status = status;
    }

    public StringFilter getRejectionReason() {
        return rejectionReason;
    }

    public Optional<StringFilter> optionalRejectionReason() {
        return Optional.ofNullable(rejectionReason);
    }

    public StringFilter rejectionReason() {
        if (rejectionReason == null) {
            setRejectionReason(new StringFilter());
        }
        return rejectionReason;
    }

    public void setRejectionReason(StringFilter rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public InstantFilter getPublishedAt() {
        return publishedAt;
    }

    public Optional<InstantFilter> optionalPublishedAt() {
        return Optional.ofNullable(publishedAt);
    }

    public InstantFilter publishedAt() {
        if (publishedAt == null) {
            setPublishedAt(new InstantFilter());
        }
        return publishedAt;
    }

    public void setPublishedAt(InstantFilter publishedAt) {
        this.publishedAt = publishedAt;
    }

    public BooleanFilter getPublishedToDiscovery() {
        return publishedToDiscovery;
    }

    public Optional<BooleanFilter> optionalPublishedToDiscovery() {
        return Optional.ofNullable(publishedToDiscovery);
    }

    public BooleanFilter publishedToDiscovery() {
        if (publishedToDiscovery == null) {
            setPublishedToDiscovery(new BooleanFilter());
        }
        return publishedToDiscovery;
    }

    public void setPublishedToDiscovery(BooleanFilter publishedToDiscovery) {
        this.publishedToDiscovery = publishedToDiscovery;
    }

    public GroupModerationStatusFilter getGroupModerationStatus() {
        return groupModerationStatus;
    }

    public Optional<GroupModerationStatusFilter> optionalGroupModerationStatus() {
        return Optional.ofNullable(groupModerationStatus);
    }

    public GroupModerationStatusFilter groupModerationStatus() {
        if (groupModerationStatus == null) {
            setGroupModerationStatus(new GroupModerationStatusFilter());
        }
        return groupModerationStatus;
    }

    public void setGroupModerationStatus(GroupModerationStatusFilter groupModerationStatus) {
        this.groupModerationStatus = groupModerationStatus;
    }

    public PlatformModerationStatusFilter getPlatformModerationStatus() {
        return platformModerationStatus;
    }

    public Optional<PlatformModerationStatusFilter> optionalPlatformModerationStatus() {
        return Optional.ofNullable(platformModerationStatus);
    }

    public PlatformModerationStatusFilter platformModerationStatus() {
        if (platformModerationStatus == null) {
            setPlatformModerationStatus(new PlatformModerationStatusFilter());
        }
        return platformModerationStatus;
    }

    public void setPlatformModerationStatus(PlatformModerationStatusFilter platformModerationStatus) {
        this.platformModerationStatus = platformModerationStatus;
    }

    public InstantFilter getCancelledAt() {
        return cancelledAt;
    }

    public Optional<InstantFilter> optionalCancelledAt() {
        return Optional.ofNullable(cancelledAt);
    }

    public InstantFilter cancelledAt() {
        if (cancelledAt == null) {
            setCancelledAt(new InstantFilter());
        }
        return cancelledAt;
    }

    public void setCancelledAt(InstantFilter cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public StringFilter getCancellationReason() {
        return cancellationReason;
    }

    public Optional<StringFilter> optionalCancellationReason() {
        return Optional.ofNullable(cancellationReason);
    }

    public StringFilter cancellationReason() {
        if (cancellationReason == null) {
            setCancellationReason(new StringFilter());
        }
        return cancellationReason;
    }

    public void setCancellationReason(StringFilter cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LongFilter getCommunityId() {
        return communityId;
    }

    public Optional<LongFilter> optionalCommunityId() {
        return Optional.ofNullable(communityId);
    }

    public LongFilter communityId() {
        if (communityId == null) {
            setCommunityId(new LongFilter());
        }
        return communityId;
    }

    public void setCommunityId(LongFilter communityId) {
        this.communityId = communityId;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public Optional<LongFilter> optionalCategoryId() {
        return Optional.ofNullable(categoryId);
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            setCategoryId(new LongFilter());
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getCreatedById() {
        return createdById;
    }

    public Optional<LongFilter> optionalCreatedById() {
        return Optional.ofNullable(createdById);
    }

    public LongFilter createdById() {
        if (createdById == null) {
            setCreatedById(new LongFilter());
        }
        return createdById;
    }

    public void setCreatedById(LongFilter createdById) {
        this.createdById = createdById;
    }

    public LongFilter getCancelledById() {
        return cancelledById;
    }

    public Optional<LongFilter> optionalCancelledById() {
        return Optional.ofNullable(cancelledById);
    }

    public LongFilter cancelledById() {
        if (cancelledById == null) {
            setCancelledById(new LongFilter());
        }
        return cancelledById;
    }

    public void setCancelledById(LongFilter cancelledById) {
        this.cancelledById = cancelledById;
    }

    public LongFilter getParticipantsId() {
        return participantsId;
    }

    public Optional<LongFilter> optionalParticipantsId() {
        return Optional.ofNullable(participantsId);
    }

    public LongFilter participantsId() {
        if (participantsId == null) {
            setParticipantsId(new LongFilter());
        }
        return participantsId;
    }

    public void setParticipantsId(LongFilter participantsId) {
        this.participantsId = participantsId;
    }

    public LongFilter getMessagesId() {
        return messagesId;
    }

    public Optional<LongFilter> optionalMessagesId() {
        return Optional.ofNullable(messagesId);
    }

    public LongFilter messagesId() {
        if (messagesId == null) {
            setMessagesId(new LongFilter());
        }
        return messagesId;
    }

    public void setMessagesId(LongFilter messagesId) {
        this.messagesId = messagesId;
    }

    public LongFilter getRatingsId() {
        return ratingsId;
    }

    public Optional<LongFilter> optionalRatingsId() {
        return Optional.ofNullable(ratingsId);
    }

    public LongFilter ratingsId() {
        if (ratingsId == null) {
            setRatingsId(new LongFilter());
        }
        return ratingsId;
    }

    public void setRatingsId(LongFilter ratingsId) {
        this.ratingsId = ratingsId;
    }

    public LongFilter getPostsId() {
        return postsId;
    }

    public Optional<LongFilter> optionalPostsId() {
        return Optional.ofNullable(postsId);
    }

    public LongFilter postsId() {
        if (postsId == null) {
            setPostsId(new LongFilter());
        }
        return postsId;
    }

    public void setPostsId(LongFilter postsId) {
        this.postsId = postsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EventCriteria that = (EventCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(startsAt, that.startsAt) &&
            Objects.equals(endsAt, that.endsAt) &&
            Objects.equals(locationName, that.locationName) &&
            Objects.equals(addressText, that.addressText) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(capacity, that.capacity) &&
            Objects.equals(status, that.status) &&
            Objects.equals(rejectionReason, that.rejectionReason) &&
            Objects.equals(publishedAt, that.publishedAt) &&
            Objects.equals(publishedToDiscovery, that.publishedToDiscovery) &&
            Objects.equals(groupModerationStatus, that.groupModerationStatus) &&
            Objects.equals(platformModerationStatus, that.platformModerationStatus) &&
            Objects.equals(cancelledAt, that.cancelledAt) &&
            Objects.equals(cancellationReason, that.cancellationReason) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(communityId, that.communityId) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(cancelledById, that.cancelledById) &&
            Objects.equals(participantsId, that.participantsId) &&
            Objects.equals(messagesId, that.messagesId) &&
            Objects.equals(ratingsId, that.ratingsId) &&
            Objects.equals(postsId, that.postsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            startsAt,
            endsAt,
            locationName,
            addressText,
            latitude,
            longitude,
            coverImageUrl,
            capacity,
            status,
            rejectionReason,
            publishedAt,
            publishedToDiscovery,
            groupModerationStatus,
            platformModerationStatus,
            cancelledAt,
            cancellationReason,
            createdAt,
            updatedAt,
            communityId,
            categoryId,
            createdById,
            cancelledById,
            participantsId,
            messagesId,
            ratingsId,
            postsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalStartsAt().map(f -> "startsAt=" + f + ", ").orElse("") +
            optionalEndsAt().map(f -> "endsAt=" + f + ", ").orElse("") +
            optionalLocationName().map(f -> "locationName=" + f + ", ").orElse("") +
            optionalAddressText().map(f -> "addressText=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalCapacity().map(f -> "capacity=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRejectionReason().map(f -> "rejectionReason=" + f + ", ").orElse("") +
            optionalPublishedAt().map(f -> "publishedAt=" + f + ", ").orElse("") +
            optionalPublishedToDiscovery().map(f -> "publishedToDiscovery=" + f + ", ").orElse("") +
            optionalGroupModerationStatus().map(f -> "groupModerationStatus=" + f + ", ").orElse("") +
            optionalPlatformModerationStatus().map(f -> "platformModerationStatus=" + f + ", ").orElse("") +
            optionalCancelledAt().map(f -> "cancelledAt=" + f + ", ").orElse("") +
            optionalCancellationReason().map(f -> "cancellationReason=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalCommunityId().map(f -> "communityId=" + f + ", ").orElse("") +
            optionalCategoryId().map(f -> "categoryId=" + f + ", ").orElse("") +
            optionalCreatedById().map(f -> "createdById=" + f + ", ").orElse("") +
            optionalCancelledById().map(f -> "cancelledById=" + f + ", ").orElse("") +
            optionalParticipantsId().map(f -> "participantsId=" + f + ", ").orElse("") +
            optionalMessagesId().map(f -> "messagesId=" + f + ", ").orElse("") +
            optionalRatingsId().map(f -> "ratingsId=" + f + ", ").orElse("") +
            optionalPostsId().map(f -> "postsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
