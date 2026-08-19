package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.GroupModerationStatus;
import com.bialem.backend.domain.enumeration.PlatformModerationStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 200)
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Size(max = 200)
    @Column(name = "location_name", length = 200)
    private String locationName;

    @Size(max = 500)
    @Column(name = "address_text", length = 500)
    private String addressText;

    @Column(name = "latitude", precision = 21, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 21, scale = 2)
    private BigDecimal longitude;

    @Size(max = 2048)
    @Column(name = "cover_image_url", length = 2048)
    private String coverImageUrl;

    @Min(value = 1)
    @Column(name = "capacity")
    private Integer capacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Size(max = 1000)
    @Column(name = "rejection_reason", length = 1000)
    private String rejectionReason;

    @Column(name = "published_at")
    private Instant publishedAt;

    @NotNull
    @Column(name = "published_to_discovery", nullable = false)
    private Boolean publishedToDiscovery;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "group_moderation_status", nullable = false)
    private GroupModerationStatus groupModerationStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "platform_moderation_status", nullable = false)
    private PlatformModerationStatus platformModerationStatus;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Size(max = 1000)
    @Column(name = "cancellation_reason", length = 1000)
    private String cancellationReason;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "parent",
            "categoryHub",
            "createdBy",
            "leadModerator",
            "children",
            "categorizedGroups",
            "members",
            "assistants",
            "events",
            "posts",
            "storyTargets",
        },
        allowSetters = true
    )
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = {
            "parent",
            "categoryHub",
            "createdBy",
            "leadModerator",
            "children",
            "categorizedGroups",
            "members",
            "assistants",
            "events",
            "posts",
            "storyTargets",
        },
        allowSetters = true
    )
    private Community category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile cancelledBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "event", "user" }, allowSetters = true)
    private Set<EventParticipant> participants = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "event", "author" }, allowSetters = true)
    private Set<EventMessage> messages = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "event", "user" }, allowSetters = true)
    private Set<EventRating> ratings = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "community", "event", "author", "media" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Event id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Event title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Event description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getStartsAt() {
        return this.startsAt;
    }

    public Event startsAt(Instant startsAt) {
        this.setStartsAt(startsAt);
        return this;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return this.endsAt;
    }

    public Event endsAt(Instant endsAt) {
        this.setEndsAt(endsAt);
        return this;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public Event locationName(String locationName) {
        this.setLocationName(locationName);
        return this;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddressText() {
        return this.addressText;
    }

    public Event addressText(String addressText) {
        this.setAddressText(addressText);
        return this;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public Event latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public Event longitude(BigDecimal longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public Event coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getCapacity() {
        return this.capacity;
    }

    public Event capacity(Integer capacity) {
        this.setCapacity(capacity);
        return this;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public EventStatus getStatus() {
        return this.status;
    }

    public Event status(EventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return this.rejectionReason;
    }

    public Event rejectionReason(String rejectionReason) {
        this.setRejectionReason(rejectionReason);
        return this;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Instant getPublishedAt() {
        return this.publishedAt;
    }

    public Event publishedAt(Instant publishedAt) {
        this.setPublishedAt(publishedAt);
        return this;
    }

    public void setPublishedAt(Instant publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Boolean getPublishedToDiscovery() {
        return this.publishedToDiscovery;
    }

    public Event publishedToDiscovery(Boolean publishedToDiscovery) {
        this.setPublishedToDiscovery(publishedToDiscovery);
        return this;
    }

    public void setPublishedToDiscovery(Boolean publishedToDiscovery) {
        this.publishedToDiscovery = publishedToDiscovery;
    }

    public GroupModerationStatus getGroupModerationStatus() {
        return this.groupModerationStatus;
    }

    public Event groupModerationStatus(GroupModerationStatus groupModerationStatus) {
        this.setGroupModerationStatus(groupModerationStatus);
        return this;
    }

    public void setGroupModerationStatus(GroupModerationStatus groupModerationStatus) {
        this.groupModerationStatus = groupModerationStatus;
    }

    public PlatformModerationStatus getPlatformModerationStatus() {
        return this.platformModerationStatus;
    }

    public Event platformModerationStatus(PlatformModerationStatus platformModerationStatus) {
        this.setPlatformModerationStatus(platformModerationStatus);
        return this;
    }

    public void setPlatformModerationStatus(PlatformModerationStatus platformModerationStatus) {
        this.platformModerationStatus = platformModerationStatus;
    }

    public Instant getCancelledAt() {
        return this.cancelledAt;
    }

    public Event cancelledAt(Instant cancelledAt) {
        this.setCancelledAt(cancelledAt);
        return this;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return this.cancellationReason;
    }

    public Event cancellationReason(String cancellationReason) {
        this.setCancellationReason(cancellationReason);
        return this;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Event createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Event updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Community getCommunity() {
        return this.community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Event community(Community community) {
        this.setCommunity(community);
        return this;
    }

    public Community getCategory() {
        return this.category;
    }

    public void setCategory(Community community) {
        this.category = community;
    }

    public Event category(Community community) {
        this.setCategory(community);
        return this;
    }

    public Profile getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Profile profile) {
        this.createdBy = profile;
    }

    public Event createdBy(Profile profile) {
        this.setCreatedBy(profile);
        return this;
    }

    public Profile getCancelledBy() {
        return this.cancelledBy;
    }

    public void setCancelledBy(Profile profile) {
        this.cancelledBy = profile;
    }

    public Event cancelledBy(Profile profile) {
        this.setCancelledBy(profile);
        return this;
    }

    public Set<EventParticipant> getParticipants() {
        return this.participants;
    }

    public void setParticipants(Set<EventParticipant> eventParticipants) {
        if (this.participants != null) {
            this.participants.forEach(i -> i.setEvent(null));
        }
        if (eventParticipants != null) {
            eventParticipants.forEach(i -> i.setEvent(this));
        }
        this.participants = eventParticipants;
    }

    public Event participants(Set<EventParticipant> eventParticipants) {
        this.setParticipants(eventParticipants);
        return this;
    }

    public Event addParticipants(EventParticipant eventParticipant) {
        this.participants.add(eventParticipant);
        eventParticipant.setEvent(this);
        return this;
    }

    public Event removeParticipants(EventParticipant eventParticipant) {
        this.participants.remove(eventParticipant);
        eventParticipant.setEvent(null);
        return this;
    }

    public Set<EventMessage> getMessages() {
        return this.messages;
    }

    public void setMessages(Set<EventMessage> eventMessages) {
        if (this.messages != null) {
            this.messages.forEach(i -> i.setEvent(null));
        }
        if (eventMessages != null) {
            eventMessages.forEach(i -> i.setEvent(this));
        }
        this.messages = eventMessages;
    }

    public Event messages(Set<EventMessage> eventMessages) {
        this.setMessages(eventMessages);
        return this;
    }

    public Event addMessages(EventMessage eventMessage) {
        this.messages.add(eventMessage);
        eventMessage.setEvent(this);
        return this;
    }

    public Event removeMessages(EventMessage eventMessage) {
        this.messages.remove(eventMessage);
        eventMessage.setEvent(null);
        return this;
    }

    public Set<EventRating> getRatings() {
        return this.ratings;
    }

    public void setRatings(Set<EventRating> eventRatings) {
        if (this.ratings != null) {
            this.ratings.forEach(i -> i.setEvent(null));
        }
        if (eventRatings != null) {
            eventRatings.forEach(i -> i.setEvent(this));
        }
        this.ratings = eventRatings;
    }

    public Event ratings(Set<EventRating> eventRatings) {
        this.setRatings(eventRatings);
        return this;
    }

    public Event addRatings(EventRating eventRating) {
        this.ratings.add(eventRating);
        eventRating.setEvent(this);
        return this;
    }

    public Event removeRatings(EventRating eventRating) {
        this.ratings.remove(eventRating);
        eventRating.setEvent(null);
        return this;
    }

    public Set<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Post> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.setEvent(null));
        }
        if (posts != null) {
            posts.forEach(i -> i.setEvent(this));
        }
        this.posts = posts;
    }

    public Event posts(Set<Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public Event addPosts(Post post) {
        this.posts.add(post);
        post.setEvent(this);
        return this;
    }

    public Event removePosts(Post post) {
        this.posts.remove(post);
        post.setEvent(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return getId() != null && getId().equals(((Event) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", endsAt='" + getEndsAt() + "'" +
            ", locationName='" + getLocationName() + "'" +
            ", addressText='" + getAddressText() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", capacity=" + getCapacity() +
            ", status='" + getStatus() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            ", publishedAt='" + getPublishedAt() + "'" +
            ", publishedToDiscovery='" + getPublishedToDiscovery() + "'" +
            ", groupModerationStatus='" + getGroupModerationStatus() + "'" +
            ", platformModerationStatus='" + getPlatformModerationStatus() + "'" +
            ", cancelledAt='" + getCancelledAt() + "'" +
            ", cancellationReason='" + getCancellationReason() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
