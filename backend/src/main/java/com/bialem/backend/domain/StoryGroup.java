package com.bialem.backend.domain;

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

/**
 * A StoryGroup.
 */
@Entity
@Table(name = "story_group")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Size(max = 200)
    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "latitude", precision = 21, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 21, scale = 2)
    private BigDecimal longitude;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile author;

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
            "community",
            "category",
            "createdBy",
            "cancelledBy",
            "participants",
            "messages",
            "ratings",
            "posts",
            "eventTickets",
        },
        allowSetters = true
    )
    private Event event;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "storyGroup")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "author", "views", "communityTargets", "storyHashtags", "storyElements", "storyGroup", "event" }, allowSetters = true)
    private Set<Story> stories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryGroup id(Long id) {
        this.setId(id);
        return this;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public StoryGroup locationName(String locationName) {
        this.setLocationName(locationName);
        return this;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public StoryGroup latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public StoryGroup longitude(BigDecimal longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public StoryGroup createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public StoryGroup expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public Profile getAuthor() {
        return author;
    }

    public void setAuthor(Profile author) {
        this.author = author;
    }

    public StoryGroup author(Profile author) {
        this.setAuthor(author);
        return this;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public StoryGroup community(Community community) {
        this.setCommunity(community);
        return this;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public StoryGroup event(Event event) {
        this.setEvent(event);
        return this;
    }

    public Set<Story> getStories() {
        return stories;
    }

    public void setStories(Set<Story> stories) {
        if (this.stories != null) {
            this.stories.forEach(i -> i.setStoryGroup(null));
        }
        if (stories != null) {
            stories.forEach(i -> i.setStoryGroup(this));
        }
        this.stories = stories;
    }

    public StoryGroup stories(Set<Story> stories) {
        this.setStories(stories);
        return this;
    }

    public StoryGroup addStories(Story story) {
        this.stories.add(story);
        story.setStoryGroup(this);
        return this;
    }

    public StoryGroup removeStories(Story story) {
        this.stories.remove(story);
        story.setStoryGroup(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryGroup)) {
            return false;
        }
        return getId() != null && getId().equals(((StoryGroup) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "StoryGroup{" +
            "id=" + getId() +
            ", locationName='" + getLocationName() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            "}";
    }
}
