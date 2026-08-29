package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.StoryContentType;
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
 * A Story.
 */
@Entity
@Table(name = "story")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Story implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false)
    private StoryContentType contentType;

    @Size(max = 500)
    @Column(name = "body", length = 500)
    private String body;

    @Size(max = 2048)
    @Column(name = "media_url", length = 2048)
    private String mediaUrl;

    @NotNull
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @NotNull
    @Column(name = "share_with_followers", nullable = false)
    private Boolean shareWithFollowers;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Size(max = 200)
    @Column(name = "location_name", length = 200)
    private String locationName;

    @Column(name = "latitude", precision = 21, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 21, scale = 2)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile author;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "story", "viewer" }, allowSetters = true)
    private Set<StoryView> views = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "story", "community" }, allowSetters = true)
    private Set<StoryCommunityTarget> communityTargets = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "story", "hashtag" }, allowSetters = true)
    private Set<StoryHashtag> storyHashtags = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "story" }, allowSetters = true)
    private Set<StoryElement> storyElements = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "stories", "author", "community", "event" }, allowSetters = true)
    private StoryGroup storyGroup;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Story id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryContentType getContentType() {
        return this.contentType;
    }

    public Story contentType(StoryContentType contentType) {
        this.setContentType(contentType);
        return this;
    }

    public void setContentType(StoryContentType contentType) {
        this.contentType = contentType;
    }

    public String getBody() {
        return this.body;
    }

    public Story body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public Story mediaUrl(String mediaUrl) {
        this.setMediaUrl(mediaUrl);
        return this;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Boolean getIsPublic() {
        return this.isPublic;
    }

    public Story isPublic(Boolean isPublic) {
        this.setIsPublic(isPublic);
        return this;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getShareWithFollowers() {
        return this.shareWithFollowers;
    }

    public Story shareWithFollowers(Boolean shareWithFollowers) {
        this.setShareWithFollowers(shareWithFollowers);
        return this;
    }

    public void setShareWithFollowers(Boolean shareWithFollowers) {
        this.shareWithFollowers = shareWithFollowers;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Story createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public Story expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public Story locationName(String locationName) {
        this.setLocationName(locationName);
        return this;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public Story latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public Story longitude(BigDecimal longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Profile getAuthor() {
        return this.author;
    }

    public void setAuthor(Profile profile) {
        this.author = profile;
    }

    public Story author(Profile profile) {
        this.setAuthor(profile);
        return this;
    }

    public Set<StoryView> getViews() {
        return this.views;
    }

    public void setViews(Set<StoryView> storyViews) {
        if (this.views != null) {
            this.views.forEach(i -> i.setStory(null));
        }
        if (storyViews != null) {
            storyViews.forEach(i -> i.setStory(this));
        }
        this.views = storyViews;
    }

    public Story views(Set<StoryView> storyViews) {
        this.setViews(storyViews);
        return this;
    }

    public Story addViews(StoryView storyView) {
        this.views.add(storyView);
        storyView.setStory(this);
        return this;
    }

    public Story removeViews(StoryView storyView) {
        this.views.remove(storyView);
        storyView.setStory(null);
        return this;
    }

    public Set<StoryCommunityTarget> getCommunityTargets() {
        return this.communityTargets;
    }

    public void setCommunityTargets(Set<StoryCommunityTarget> storyCommunityTargets) {
        if (this.communityTargets != null) {
            this.communityTargets.forEach(i -> i.setStory(null));
        }
        if (storyCommunityTargets != null) {
            storyCommunityTargets.forEach(i -> i.setStory(this));
        }
        this.communityTargets = storyCommunityTargets;
    }

    public Story communityTargets(Set<StoryCommunityTarget> storyCommunityTargets) {
        this.setCommunityTargets(storyCommunityTargets);
        return this;
    }

    public Story addCommunityTargets(StoryCommunityTarget storyCommunityTarget) {
        this.communityTargets.add(storyCommunityTarget);
        storyCommunityTarget.setStory(this);
        return this;
    }

    public Story removeCommunityTargets(StoryCommunityTarget storyCommunityTarget) {
        this.communityTargets.remove(storyCommunityTarget);
        storyCommunityTarget.setStory(null);
        return this;
    }

    public Set<StoryHashtag> getStoryHashtags() {
        return this.storyHashtags;
    }

    public void setStoryHashtags(Set<StoryHashtag> storyHashtags) {
        if (this.storyHashtags != null) {
            this.storyHashtags.forEach(i -> i.setStory(null));
        }
        if (storyHashtags != null) {
            storyHashtags.forEach(i -> i.setStory(this));
        }
        this.storyHashtags = storyHashtags;
    }

    public Story storyHashtags(Set<StoryHashtag> storyHashtags) {
        this.setStoryHashtags(storyHashtags);
        return this;
    }

    public Story addStoryHashtags(StoryHashtag storyHashtag) {
        this.storyHashtags.add(storyHashtag);
        storyHashtag.setStory(this);
        return this;
    }

    public Story removeStoryHashtags(StoryHashtag storyHashtag) {
        this.storyHashtags.remove(storyHashtag);
        storyHashtag.setStory(null);
        return this;
    }

    public Set<StoryElement> getStoryElements() {
        return this.storyElements;
    }

    public void setStoryElements(Set<StoryElement> storyElements) {
        if (this.storyElements != null) {
            this.storyElements.forEach(i -> i.setStory(null));
        }
        if (storyElements != null) {
            storyElements.forEach(i -> i.setStory(this));
        }
        this.storyElements = storyElements;
    }

    public Story storyElements(Set<StoryElement> storyElements) {
        this.setStoryElements(storyElements);
        return this;
    }

    public Story addStoryElements(StoryElement storyElement) {
        this.storyElements.add(storyElement);
        storyElement.setStory(this);
        return this;
    }

    public Story removeStoryElements(StoryElement storyElement) {
        this.storyElements.remove(storyElement);
        storyElement.setStory(null);
        return this;
    }

    public StoryGroup getStoryGroup() {
        return this.storyGroup;
    }

    public void setStoryGroup(StoryGroup storyGroup) {
        this.storyGroup = storyGroup;
    }

    public Story storyGroup(StoryGroup storyGroup) {
        this.setStoryGroup(storyGroup);
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Story event(Event event) {
        this.setEvent(event);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Story)) {
            return false;
        }
        return getId() != null && getId().equals(((Story) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Story{" +
            "id=" + getId() +
            ", contentType='" + getContentType() + "'" +
            ", body='" + getBody() + "'" +
            ", mediaUrl='" + getMediaUrl() + "'" +
            ", isPublic='" + getIsPublic() + "'" +
            ", shareWithFollowers='" + getShareWithFollowers() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", locationName='" + getLocationName() + "'" +
            "}";
    }
}
