package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.ModerationStatus;
import com.bialem.backend.domain.enumeration.PostVisibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A Post.
 */
@Entity
@Table(name = "post")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "body")
    private String body;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private PostVisibility visibility;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false)
    private ModerationStatus moderationStatus;

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
        value = { "community", "category", "createdBy", "cancelledBy", "participants", "messages", "ratings", "posts" },
        allowSetters = true
    )
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile author;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "post" }, allowSetters = true)
    private Set<PostMedia> media = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Post id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return this.body;
    }

    public Post body(String body) {
        this.setBody(body);
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public PostVisibility getVisibility() {
        return this.visibility;
    }

    public Post visibility(PostVisibility visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(PostVisibility visibility) {
        this.visibility = visibility;
    }

    public ModerationStatus getModerationStatus() {
        return this.moderationStatus;
    }

    public Post moderationStatus(ModerationStatus moderationStatus) {
        this.setModerationStatus(moderationStatus);
        return this;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Post createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Post updatedAt(Instant updatedAt) {
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

    public Post community(Community community) {
        this.setCommunity(community);
        return this;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Post event(Event event) {
        this.setEvent(event);
        return this;
    }

    public Profile getAuthor() {
        return this.author;
    }

    public void setAuthor(Profile profile) {
        this.author = profile;
    }

    public Post author(Profile profile) {
        this.setAuthor(profile);
        return this;
    }

    public Set<PostMedia> getMedia() {
        return this.media;
    }

    public void setMedia(Set<PostMedia> postMedias) {
        if (this.media != null) {
            this.media.forEach(i -> i.setPost(null));
        }
        if (postMedias != null) {
            postMedias.forEach(i -> i.setPost(this));
        }
        this.media = postMedias;
    }

    public Post media(Set<PostMedia> postMedias) {
        this.setMedia(postMedias);
        return this;
    }

    public Post addMedia(PostMedia postMedia) {
        this.media.add(postMedia);
        postMedia.setPost(this);
        return this;
    }

    public Post removeMedia(PostMedia postMedia) {
        this.media.remove(postMedia);
        postMedia.setPost(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        return getId() != null && getId().equals(((Post) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", body='" + getBody() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", moderationStatus='" + getModerationStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
