package com.bialem.backend.service.criteria;

import com.bialem.backend.domain.enumeration.ProfileStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.bialem.backend.domain.Profile} entity. This class is used
 * in {@link com.bialem.backend.web.rest.ProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProfileCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProfileStatus
     */
    public static class ProfileStatusFilter extends Filter<ProfileStatus> {

        public ProfileStatusFilter() {}

        public ProfileStatusFilter(ProfileStatusFilter filter) {
            super(filter);
        }

        @Override
        public ProfileStatusFilter copy() {
            return new ProfileStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter displayName;

    private StringFilter username;

    private StringFilter avatarUrl;

    private StringFilter bio;

    private StringFilter city;

    private ProfileStatusFilter status;

    private BooleanFilter isVerified;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter userId;

    private LongFilter preferencesId;

    private Boolean distinct;

    public ProfileCriteria() {}

    public ProfileCriteria(ProfileCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.displayName = other.optionalDisplayName().map(StringFilter::copy).orElse(null);
        this.username = other.optionalUsername().map(StringFilter::copy).orElse(null);
        this.avatarUrl = other.optionalAvatarUrl().map(StringFilter::copy).orElse(null);
        this.bio = other.optionalBio().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ProfileStatusFilter::copy).orElse(null);
        this.isVerified = other.optionalIsVerified().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.preferencesId = other.optionalPreferencesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProfileCriteria copy() {
        return new ProfileCriteria(this);
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

    public StringFilter getDisplayName() {
        return displayName;
    }

    public Optional<StringFilter> optionalDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public StringFilter displayName() {
        if (displayName == null) {
            setDisplayName(new StringFilter());
        }
        return displayName;
    }

    public void setDisplayName(StringFilter displayName) {
        this.displayName = displayName;
    }

    public StringFilter getUsername() {
        return username;
    }

    public Optional<StringFilter> optionalUsername() {
        return Optional.ofNullable(username);
    }

    public StringFilter username() {
        if (username == null) {
            setUsername(new StringFilter());
        }
        return username;
    }

    public void setUsername(StringFilter username) {
        this.username = username;
    }

    public StringFilter getAvatarUrl() {
        return avatarUrl;
    }

    public Optional<StringFilter> optionalAvatarUrl() {
        return Optional.ofNullable(avatarUrl);
    }

    public StringFilter avatarUrl() {
        if (avatarUrl == null) {
            setAvatarUrl(new StringFilter());
        }
        return avatarUrl;
    }

    public void setAvatarUrl(StringFilter avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public StringFilter getBio() {
        return bio;
    }

    public Optional<StringFilter> optionalBio() {
        return Optional.ofNullable(bio);
    }

    public StringFilter bio() {
        if (bio == null) {
            setBio(new StringFilter());
        }
        return bio;
    }

    public void setBio(StringFilter bio) {
        this.bio = bio;
    }

    public StringFilter getCity() {
        return city;
    }

    public Optional<StringFilter> optionalCity() {
        return Optional.ofNullable(city);
    }

    public StringFilter city() {
        if (city == null) {
            setCity(new StringFilter());
        }
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public ProfileStatusFilter getStatus() {
        return status;
    }

    public Optional<ProfileStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ProfileStatusFilter status() {
        if (status == null) {
            setStatus(new ProfileStatusFilter());
        }
        return status;
    }

    public void setStatus(ProfileStatusFilter status) {
        this.status = status;
    }

    public BooleanFilter getIsVerified() {
        return isVerified;
    }

    public Optional<BooleanFilter> optionalIsVerified() {
        return Optional.ofNullable(isVerified);
    }

    public BooleanFilter isVerified() {
        if (isVerified == null) {
            setIsVerified(new BooleanFilter());
        }
        return isVerified;
    }

    public void setIsVerified(BooleanFilter isVerified) {
        this.isVerified = isVerified;
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

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPreferencesId() {
        return preferencesId;
    }

    public Optional<LongFilter> optionalPreferencesId() {
        return Optional.ofNullable(preferencesId);
    }

    public LongFilter preferencesId() {
        if (preferencesId == null) {
            setPreferencesId(new LongFilter());
        }
        return preferencesId;
    }

    public void setPreferencesId(LongFilter preferencesId) {
        this.preferencesId = preferencesId;
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
        final ProfileCriteria that = (ProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(displayName, that.displayName) &&
            Objects.equals(username, that.username) &&
            Objects.equals(avatarUrl, that.avatarUrl) &&
            Objects.equals(bio, that.bio) &&
            Objects.equals(city, that.city) &&
            Objects.equals(status, that.status) &&
            Objects.equals(isVerified, that.isVerified) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(preferencesId, that.preferencesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            displayName,
            username,
            avatarUrl,
            bio,
            city,
            status,
            isVerified,
            createdAt,
            updatedAt,
            userId,
            preferencesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalDisplayName().map(f -> "displayName=" + f + ", ").orElse("") +
            optionalUsername().map(f -> "username=" + f + ", ").orElse("") +
            optionalAvatarUrl().map(f -> "avatarUrl=" + f + ", ").orElse("") +
            optionalBio().map(f -> "bio=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalIsVerified().map(f -> "isVerified=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalPreferencesId().map(f -> "preferencesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
