package com.bialem.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A StoryHashtag.
 */
@Entity
@Table(name = "story_hashtag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StoryHashtag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "author", "views", "communityTargets", "storyHashtags", "storyElements", "storyGroup", "event" }, allowSetters = true)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "storyHashtags" }, allowSetters = true)
    private Hashtag hashtag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StoryHashtag id(Long id) {
        this.setId(id);
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public StoryHashtag createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public Story getStory() {
        return story;
    }

    public void setStory(Story story) {
        this.story = story;
    }

    public StoryHashtag story(Story story) {
        this.setStory(story);
        return this;
    }

    public Hashtag getHashtag() {
        return hashtag;
    }

    public void setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    public StoryHashtag hashtag(Hashtag hashtag) {
        this.setHashtag(hashtag);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StoryHashtag)) {
            return false;
        }
        return getId() != null && getId().equals(((StoryHashtag) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "StoryHashtag{" +
            "id=" + getId() +
            "}";
    }
}
