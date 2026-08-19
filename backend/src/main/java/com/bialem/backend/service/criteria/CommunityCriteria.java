package com.bialem.backend.service.criteria;

import com.bialem.backend.domain.enumeration.CommunityType;
import com.bialem.backend.domain.enumeration.CommunityVisibility;
import com.bialem.backend.domain.enumeration.PartnerTrustLevel;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.bialem.backend.domain.Community} entity. This class is used
 * in {@link com.bialem.backend.web.rest.CommunityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /communities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommunityCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CommunityVisibility
     */
    public static class CommunityVisibilityFilter extends Filter<CommunityVisibility> {

        public CommunityVisibilityFilter() {}

        public CommunityVisibilityFilter(CommunityVisibilityFilter filter) {
            super(filter);
        }

        @Override
        public CommunityVisibilityFilter copy() {
            return new CommunityVisibilityFilter(this);
        }
    }

    /**
     * Class for filtering CommunityType
     */
    public static class CommunityTypeFilter extends Filter<CommunityType> {

        public CommunityTypeFilter() {}

        public CommunityTypeFilter(CommunityTypeFilter filter) {
            super(filter);
        }

        @Override
        public CommunityTypeFilter copy() {
            return new CommunityTypeFilter(this);
        }
    }

    /**
     * Class for filtering PartnerTrustLevel
     */
    public static class PartnerTrustLevelFilter extends Filter<PartnerTrustLevel> {

        public PartnerTrustLevelFilter() {}

        public PartnerTrustLevelFilter(PartnerTrustLevelFilter filter) {
            super(filter);
        }

        @Override
        public PartnerTrustLevelFilter copy() {
            return new PartnerTrustLevelFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter slug;

    private CommunityVisibilityFilter visibility;

    private StringFilter coverImageUrl;

    private CommunityTypeFilter communityType;

    private PartnerTrustLevelFilter partnerTrustLevel;

    private BooleanFilter isVerifiedPartner;

    private BooleanFilter isDiscoverable;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter parentId;

    private LongFilter categoryHubId;

    private LongFilter createdById;

    private LongFilter leadModeratorId;

    private LongFilter childrenId;

    private LongFilter categorizedGroupsId;

    private LongFilter membersId;

    private LongFilter assistantsId;

    private LongFilter eventsId;

    private LongFilter postsId;

    private LongFilter storyTargetsId;

    private Boolean distinct;

    public CommunityCriteria() {}

    public CommunityCriteria(CommunityCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.visibility = other.optionalVisibility().map(CommunityVisibilityFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.communityType = other.optionalCommunityType().map(CommunityTypeFilter::copy).orElse(null);
        this.partnerTrustLevel = other.optionalPartnerTrustLevel().map(PartnerTrustLevelFilter::copy).orElse(null);
        this.isVerifiedPartner = other.optionalIsVerifiedPartner().map(BooleanFilter::copy).orElse(null);
        this.isDiscoverable = other.optionalIsDiscoverable().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.parentId = other.optionalParentId().map(LongFilter::copy).orElse(null);
        this.categoryHubId = other.optionalCategoryHubId().map(LongFilter::copy).orElse(null);
        this.createdById = other.optionalCreatedById().map(LongFilter::copy).orElse(null);
        this.leadModeratorId = other.optionalLeadModeratorId().map(LongFilter::copy).orElse(null);
        this.childrenId = other.optionalChildrenId().map(LongFilter::copy).orElse(null);
        this.categorizedGroupsId = other.optionalCategorizedGroupsId().map(LongFilter::copy).orElse(null);
        this.membersId = other.optionalMembersId().map(LongFilter::copy).orElse(null);
        this.assistantsId = other.optionalAssistantsId().map(LongFilter::copy).orElse(null);
        this.eventsId = other.optionalEventsId().map(LongFilter::copy).orElse(null);
        this.postsId = other.optionalPostsId().map(LongFilter::copy).orElse(null);
        this.storyTargetsId = other.optionalStoryTargetsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CommunityCriteria copy() {
        return new CommunityCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getSlug() {
        return slug;
    }

    public Optional<StringFilter> optionalSlug() {
        return Optional.ofNullable(slug);
    }

    public StringFilter slug() {
        if (slug == null) {
            setSlug(new StringFilter());
        }
        return slug;
    }

    public void setSlug(StringFilter slug) {
        this.slug = slug;
    }

    public CommunityVisibilityFilter getVisibility() {
        return visibility;
    }

    public Optional<CommunityVisibilityFilter> optionalVisibility() {
        return Optional.ofNullable(visibility);
    }

    public CommunityVisibilityFilter visibility() {
        if (visibility == null) {
            setVisibility(new CommunityVisibilityFilter());
        }
        return visibility;
    }

    public void setVisibility(CommunityVisibilityFilter visibility) {
        this.visibility = visibility;
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

    public CommunityTypeFilter getCommunityType() {
        return communityType;
    }

    public Optional<CommunityTypeFilter> optionalCommunityType() {
        return Optional.ofNullable(communityType);
    }

    public CommunityTypeFilter communityType() {
        if (communityType == null) {
            setCommunityType(new CommunityTypeFilter());
        }
        return communityType;
    }

    public void setCommunityType(CommunityTypeFilter communityType) {
        this.communityType = communityType;
    }

    public PartnerTrustLevelFilter getPartnerTrustLevel() {
        return partnerTrustLevel;
    }

    public Optional<PartnerTrustLevelFilter> optionalPartnerTrustLevel() {
        return Optional.ofNullable(partnerTrustLevel);
    }

    public PartnerTrustLevelFilter partnerTrustLevel() {
        if (partnerTrustLevel == null) {
            setPartnerTrustLevel(new PartnerTrustLevelFilter());
        }
        return partnerTrustLevel;
    }

    public void setPartnerTrustLevel(PartnerTrustLevelFilter partnerTrustLevel) {
        this.partnerTrustLevel = partnerTrustLevel;
    }

    public BooleanFilter getIsVerifiedPartner() {
        return isVerifiedPartner;
    }

    public Optional<BooleanFilter> optionalIsVerifiedPartner() {
        return Optional.ofNullable(isVerifiedPartner);
    }

    public BooleanFilter isVerifiedPartner() {
        if (isVerifiedPartner == null) {
            setIsVerifiedPartner(new BooleanFilter());
        }
        return isVerifiedPartner;
    }

    public void setIsVerifiedPartner(BooleanFilter isVerifiedPartner) {
        this.isVerifiedPartner = isVerifiedPartner;
    }

    public BooleanFilter getIsDiscoverable() {
        return isDiscoverable;
    }

    public Optional<BooleanFilter> optionalIsDiscoverable() {
        return Optional.ofNullable(isDiscoverable);
    }

    public BooleanFilter isDiscoverable() {
        if (isDiscoverable == null) {
            setIsDiscoverable(new BooleanFilter());
        }
        return isDiscoverable;
    }

    public void setIsDiscoverable(BooleanFilter isDiscoverable) {
        this.isDiscoverable = isDiscoverable;
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

    public LongFilter getParentId() {
        return parentId;
    }

    public Optional<LongFilter> optionalParentId() {
        return Optional.ofNullable(parentId);
    }

    public LongFilter parentId() {
        if (parentId == null) {
            setParentId(new LongFilter());
        }
        return parentId;
    }

    public void setParentId(LongFilter parentId) {
        this.parentId = parentId;
    }

    public LongFilter getCategoryHubId() {
        return categoryHubId;
    }

    public Optional<LongFilter> optionalCategoryHubId() {
        return Optional.ofNullable(categoryHubId);
    }

    public LongFilter categoryHubId() {
        if (categoryHubId == null) {
            setCategoryHubId(new LongFilter());
        }
        return categoryHubId;
    }

    public void setCategoryHubId(LongFilter categoryHubId) {
        this.categoryHubId = categoryHubId;
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

    public LongFilter getLeadModeratorId() {
        return leadModeratorId;
    }

    public Optional<LongFilter> optionalLeadModeratorId() {
        return Optional.ofNullable(leadModeratorId);
    }

    public LongFilter leadModeratorId() {
        if (leadModeratorId == null) {
            setLeadModeratorId(new LongFilter());
        }
        return leadModeratorId;
    }

    public void setLeadModeratorId(LongFilter leadModeratorId) {
        this.leadModeratorId = leadModeratorId;
    }

    public LongFilter getChildrenId() {
        return childrenId;
    }

    public Optional<LongFilter> optionalChildrenId() {
        return Optional.ofNullable(childrenId);
    }

    public LongFilter childrenId() {
        if (childrenId == null) {
            setChildrenId(new LongFilter());
        }
        return childrenId;
    }

    public void setChildrenId(LongFilter childrenId) {
        this.childrenId = childrenId;
    }

    public LongFilter getCategorizedGroupsId() {
        return categorizedGroupsId;
    }

    public Optional<LongFilter> optionalCategorizedGroupsId() {
        return Optional.ofNullable(categorizedGroupsId);
    }

    public LongFilter categorizedGroupsId() {
        if (categorizedGroupsId == null) {
            setCategorizedGroupsId(new LongFilter());
        }
        return categorizedGroupsId;
    }

    public void setCategorizedGroupsId(LongFilter categorizedGroupsId) {
        this.categorizedGroupsId = categorizedGroupsId;
    }

    public LongFilter getMembersId() {
        return membersId;
    }

    public Optional<LongFilter> optionalMembersId() {
        return Optional.ofNullable(membersId);
    }

    public LongFilter membersId() {
        if (membersId == null) {
            setMembersId(new LongFilter());
        }
        return membersId;
    }

    public void setMembersId(LongFilter membersId) {
        this.membersId = membersId;
    }

    public LongFilter getAssistantsId() {
        return assistantsId;
    }

    public Optional<LongFilter> optionalAssistantsId() {
        return Optional.ofNullable(assistantsId);
    }

    public LongFilter assistantsId() {
        if (assistantsId == null) {
            setAssistantsId(new LongFilter());
        }
        return assistantsId;
    }

    public void setAssistantsId(LongFilter assistantsId) {
        this.assistantsId = assistantsId;
    }

    public LongFilter getEventsId() {
        return eventsId;
    }

    public Optional<LongFilter> optionalEventsId() {
        return Optional.ofNullable(eventsId);
    }

    public LongFilter eventsId() {
        if (eventsId == null) {
            setEventsId(new LongFilter());
        }
        return eventsId;
    }

    public void setEventsId(LongFilter eventsId) {
        this.eventsId = eventsId;
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

    public LongFilter getStoryTargetsId() {
        return storyTargetsId;
    }

    public Optional<LongFilter> optionalStoryTargetsId() {
        return Optional.ofNullable(storyTargetsId);
    }

    public LongFilter storyTargetsId() {
        if (storyTargetsId == null) {
            setStoryTargetsId(new LongFilter());
        }
        return storyTargetsId;
    }

    public void setStoryTargetsId(LongFilter storyTargetsId) {
        this.storyTargetsId = storyTargetsId;
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
        final CommunityCriteria that = (CommunityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(visibility, that.visibility) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(communityType, that.communityType) &&
            Objects.equals(partnerTrustLevel, that.partnerTrustLevel) &&
            Objects.equals(isVerifiedPartner, that.isVerifiedPartner) &&
            Objects.equals(isDiscoverable, that.isDiscoverable) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(parentId, that.parentId) &&
            Objects.equals(categoryHubId, that.categoryHubId) &&
            Objects.equals(createdById, that.createdById) &&
            Objects.equals(leadModeratorId, that.leadModeratorId) &&
            Objects.equals(childrenId, that.childrenId) &&
            Objects.equals(categorizedGroupsId, that.categorizedGroupsId) &&
            Objects.equals(membersId, that.membersId) &&
            Objects.equals(assistantsId, that.assistantsId) &&
            Objects.equals(eventsId, that.eventsId) &&
            Objects.equals(postsId, that.postsId) &&
            Objects.equals(storyTargetsId, that.storyTargetsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            slug,
            visibility,
            coverImageUrl,
            communityType,
            partnerTrustLevel,
            isVerifiedPartner,
            isDiscoverable,
            createdAt,
            updatedAt,
            parentId,
            categoryHubId,
            createdById,
            leadModeratorId,
            childrenId,
            categorizedGroupsId,
            membersId,
            assistantsId,
            eventsId,
            postsId,
            storyTargetsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CommunityCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalVisibility().map(f -> "visibility=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalCommunityType().map(f -> "communityType=" + f + ", ").orElse("") +
            optionalPartnerTrustLevel().map(f -> "partnerTrustLevel=" + f + ", ").orElse("") +
            optionalIsVerifiedPartner().map(f -> "isVerifiedPartner=" + f + ", ").orElse("") +
            optionalIsDiscoverable().map(f -> "isDiscoverable=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalParentId().map(f -> "parentId=" + f + ", ").orElse("") +
            optionalCategoryHubId().map(f -> "categoryHubId=" + f + ", ").orElse("") +
            optionalCreatedById().map(f -> "createdById=" + f + ", ").orElse("") +
            optionalLeadModeratorId().map(f -> "leadModeratorId=" + f + ", ").orElse("") +
            optionalChildrenId().map(f -> "childrenId=" + f + ", ").orElse("") +
            optionalCategorizedGroupsId().map(f -> "categorizedGroupsId=" + f + ", ").orElse("") +
            optionalMembersId().map(f -> "membersId=" + f + ", ").orElse("") +
            optionalAssistantsId().map(f -> "assistantsId=" + f + ", ").orElse("") +
            optionalEventsId().map(f -> "eventsId=" + f + ", ").orElse("") +
            optionalPostsId().map(f -> "postsId=" + f + ", ").orElse("") +
            optionalStoryTargetsId().map(f -> "storyTargetsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
