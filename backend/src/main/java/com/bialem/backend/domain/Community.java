package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.CommunityType;
import com.bialem.backend.domain.enumeration.CommunityVisibility;
import com.bialem.backend.domain.enumeration.PartnerTrustLevel;
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
 * A Community.
 */
@Entity
@Table(name = "community")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Community implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column(name = "name", length = 160, nullable = false)
    private String name;

    @NotNull
    @Size(max = 80)
    @Column(name = "slug", length = 80, nullable = false, unique = true)
    private String slug;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private CommunityVisibility visibility;

    @Size(max = 2048)
    @Column(name = "cover_image_url", length = 2048)
    private String coverImageUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "community_type", nullable = false)
    private CommunityType communityType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_trust_level", nullable = false)
    private PartnerTrustLevel partnerTrustLevel;

    @NotNull
    @Column(name = "is_verified_partner", nullable = false)
    private Boolean isVerifiedPartner;

    @NotNull
    @Column(name = "is_discoverable", nullable = false)
    private Boolean isDiscoverable;

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
    private Community parent;

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
    private Community categoryHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile leadModerator;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Community> children = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "categoryHub")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
    private Set<Community> categorizedGroups = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "community", "user" }, allowSetters = true)
    private Set<CommunityMember> members = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "community", "user" }, allowSetters = true)
    private Set<CommunityModeratorAssistant> assistants = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(
        value = { "community", "category", "createdBy", "cancelledBy", "participants", "messages", "ratings", "posts" },
        allowSetters = true
    )
    private Set<Event> events = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "community", "event", "author", "media" }, allowSetters = true)
    private Set<Post> posts = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "community")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "story", "community" }, allowSetters = true)
    private Set<StoryCommunityTarget> storyTargets = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Community id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Community name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public Community slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public Community description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CommunityVisibility getVisibility() {
        return this.visibility;
    }

    public Community visibility(CommunityVisibility visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(CommunityVisibility visibility) {
        this.visibility = visibility;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public Community coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public CommunityType getCommunityType() {
        return this.communityType;
    }

    public Community communityType(CommunityType communityType) {
        this.setCommunityType(communityType);
        return this;
    }

    public void setCommunityType(CommunityType communityType) {
        this.communityType = communityType;
    }

    public PartnerTrustLevel getPartnerTrustLevel() {
        return this.partnerTrustLevel;
    }

    public Community partnerTrustLevel(PartnerTrustLevel partnerTrustLevel) {
        this.setPartnerTrustLevel(partnerTrustLevel);
        return this;
    }

    public void setPartnerTrustLevel(PartnerTrustLevel partnerTrustLevel) {
        this.partnerTrustLevel = partnerTrustLevel;
    }

    public Boolean getIsVerifiedPartner() {
        return this.isVerifiedPartner;
    }

    public Community isVerifiedPartner(Boolean isVerifiedPartner) {
        this.setIsVerifiedPartner(isVerifiedPartner);
        return this;
    }

    public void setIsVerifiedPartner(Boolean isVerifiedPartner) {
        this.isVerifiedPartner = isVerifiedPartner;
    }

    public Boolean getIsDiscoverable() {
        return this.isDiscoverable;
    }

    public Community isDiscoverable(Boolean isDiscoverable) {
        this.setIsDiscoverable(isDiscoverable);
        return this;
    }

    public void setIsDiscoverable(Boolean isDiscoverable) {
        this.isDiscoverable = isDiscoverable;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Community createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Community updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Community getParent() {
        return this.parent;
    }

    public void setParent(Community community) {
        this.parent = community;
    }

    public Community parent(Community community) {
        this.setParent(community);
        return this;
    }

    public Community getCategoryHub() {
        return this.categoryHub;
    }

    public void setCategoryHub(Community community) {
        this.categoryHub = community;
    }

    public Community categoryHub(Community community) {
        this.setCategoryHub(community);
        return this;
    }

    public Profile getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(Profile profile) {
        this.createdBy = profile;
    }

    public Community createdBy(Profile profile) {
        this.setCreatedBy(profile);
        return this;
    }

    public Profile getLeadModerator() {
        return this.leadModerator;
    }

    public void setLeadModerator(Profile profile) {
        this.leadModerator = profile;
    }

    public Community leadModerator(Profile profile) {
        this.setLeadModerator(profile);
        return this;
    }

    public Set<Community> getChildren() {
        return this.children;
    }

    public void setChildren(Set<Community> communities) {
        if (this.children != null) {
            this.children.forEach(i -> i.setParent(null));
        }
        if (communities != null) {
            communities.forEach(i -> i.setParent(this));
        }
        this.children = communities;
    }

    public Community children(Set<Community> communities) {
        this.setChildren(communities);
        return this;
    }

    public Community addChildren(Community community) {
        this.children.add(community);
        community.setParent(this);
        return this;
    }

    public Community removeChildren(Community community) {
        this.children.remove(community);
        community.setParent(null);
        return this;
    }

    public Set<Community> getCategorizedGroups() {
        return this.categorizedGroups;
    }

    public void setCategorizedGroups(Set<Community> communities) {
        if (this.categorizedGroups != null) {
            this.categorizedGroups.forEach(i -> i.setCategoryHub(null));
        }
        if (communities != null) {
            communities.forEach(i -> i.setCategoryHub(this));
        }
        this.categorizedGroups = communities;
    }

    public Community categorizedGroups(Set<Community> communities) {
        this.setCategorizedGroups(communities);
        return this;
    }

    public Community addCategorizedGroups(Community community) {
        this.categorizedGroups.add(community);
        community.setCategoryHub(this);
        return this;
    }

    public Community removeCategorizedGroups(Community community) {
        this.categorizedGroups.remove(community);
        community.setCategoryHub(null);
        return this;
    }

    public Set<CommunityMember> getMembers() {
        return this.members;
    }

    public void setMembers(Set<CommunityMember> communityMembers) {
        if (this.members != null) {
            this.members.forEach(i -> i.setCommunity(null));
        }
        if (communityMembers != null) {
            communityMembers.forEach(i -> i.setCommunity(this));
        }
        this.members = communityMembers;
    }

    public Community members(Set<CommunityMember> communityMembers) {
        this.setMembers(communityMembers);
        return this;
    }

    public Community addMembers(CommunityMember communityMember) {
        this.members.add(communityMember);
        communityMember.setCommunity(this);
        return this;
    }

    public Community removeMembers(CommunityMember communityMember) {
        this.members.remove(communityMember);
        communityMember.setCommunity(null);
        return this;
    }

    public Set<CommunityModeratorAssistant> getAssistants() {
        return this.assistants;
    }

    public void setAssistants(Set<CommunityModeratorAssistant> communityModeratorAssistants) {
        if (this.assistants != null) {
            this.assistants.forEach(i -> i.setCommunity(null));
        }
        if (communityModeratorAssistants != null) {
            communityModeratorAssistants.forEach(i -> i.setCommunity(this));
        }
        this.assistants = communityModeratorAssistants;
    }

    public Community assistants(Set<CommunityModeratorAssistant> communityModeratorAssistants) {
        this.setAssistants(communityModeratorAssistants);
        return this;
    }

    public Community addAssistants(CommunityModeratorAssistant communityModeratorAssistant) {
        this.assistants.add(communityModeratorAssistant);
        communityModeratorAssistant.setCommunity(this);
        return this;
    }

    public Community removeAssistants(CommunityModeratorAssistant communityModeratorAssistant) {
        this.assistants.remove(communityModeratorAssistant);
        communityModeratorAssistant.setCommunity(null);
        return this;
    }

    public Set<Event> getEvents() {
        return this.events;
    }

    public void setEvents(Set<Event> events) {
        if (this.events != null) {
            this.events.forEach(i -> i.setCommunity(null));
        }
        if (events != null) {
            events.forEach(i -> i.setCommunity(this));
        }
        this.events = events;
    }

    public Community events(Set<Event> events) {
        this.setEvents(events);
        return this;
    }

    public Community addEvents(Event event) {
        this.events.add(event);
        event.setCommunity(this);
        return this;
    }

    public Community removeEvents(Event event) {
        this.events.remove(event);
        event.setCommunity(null);
        return this;
    }

    public Set<Post> getPosts() {
        return this.posts;
    }

    public void setPosts(Set<Post> posts) {
        if (this.posts != null) {
            this.posts.forEach(i -> i.setCommunity(null));
        }
        if (posts != null) {
            posts.forEach(i -> i.setCommunity(this));
        }
        this.posts = posts;
    }

    public Community posts(Set<Post> posts) {
        this.setPosts(posts);
        return this;
    }

    public Community addPosts(Post post) {
        this.posts.add(post);
        post.setCommunity(this);
        return this;
    }

    public Community removePosts(Post post) {
        this.posts.remove(post);
        post.setCommunity(null);
        return this;
    }

    public Set<StoryCommunityTarget> getStoryTargets() {
        return this.storyTargets;
    }

    public void setStoryTargets(Set<StoryCommunityTarget> storyCommunityTargets) {
        if (this.storyTargets != null) {
            this.storyTargets.forEach(i -> i.setCommunity(null));
        }
        if (storyCommunityTargets != null) {
            storyCommunityTargets.forEach(i -> i.setCommunity(this));
        }
        this.storyTargets = storyCommunityTargets;
    }

    public Community storyTargets(Set<StoryCommunityTarget> storyCommunityTargets) {
        this.setStoryTargets(storyCommunityTargets);
        return this;
    }

    public Community addStoryTargets(StoryCommunityTarget storyCommunityTarget) {
        this.storyTargets.add(storyCommunityTarget);
        storyCommunityTarget.setCommunity(this);
        return this;
    }

    public Community removeStoryTargets(StoryCommunityTarget storyCommunityTarget) {
        this.storyTargets.remove(storyCommunityTarget);
        storyCommunityTarget.setCommunity(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Community)) {
            return false;
        }
        return getId() != null && getId().equals(((Community) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Community{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", communityType='" + getCommunityType() + "'" +
            ", partnerTrustLevel='" + getPartnerTrustLevel() + "'" +
            ", isVerifiedPartner='" + getIsVerifiedPartner() + "'" +
            ", isDiscoverable='" + getIsDiscoverable() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
