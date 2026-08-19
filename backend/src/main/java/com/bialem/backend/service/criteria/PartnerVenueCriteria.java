package com.bialem.backend.service.criteria;

import com.bialem.backend.domain.enumeration.PartnerVenueCategory;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.bialem.backend.domain.PartnerVenue} entity. This class is used
 * in {@link com.bialem.backend.web.rest.PartnerVenueResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /partner-venues?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerVenueCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PartnerVenueCategory
     */
    public static class PartnerVenueCategoryFilter extends Filter<PartnerVenueCategory> {

        public PartnerVenueCategoryFilter() {}

        public PartnerVenueCategoryFilter(PartnerVenueCategoryFilter filter) {
            super(filter);
        }

        @Override
        public PartnerVenueCategoryFilter copy() {
            return new PartnerVenueCategoryFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter slug;

    private PartnerVenueCategoryFilter category;

    private StringFilter logoUrl;

    private StringFilter coverImageUrl;

    private StringFilter address;

    private StringFilter city;

    private BigDecimalFilter latitude;

    private BigDecimalFilter longitude;

    private StringFilter phone;

    private StringFilter websiteUrl;

    private StringFilter instagramUrl;

    private BooleanFilter isFeatured;

    private BooleanFilter isActive;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private LongFilter offersId;

    private LongFilter staffId;

    private Boolean distinct;

    public PartnerVenueCriteria() {}

    public PartnerVenueCriteria(PartnerVenueCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.slug = other.optionalSlug().map(StringFilter::copy).orElse(null);
        this.category = other.optionalCategory().map(PartnerVenueCategoryFilter::copy).orElse(null);
        this.logoUrl = other.optionalLogoUrl().map(StringFilter::copy).orElse(null);
        this.coverImageUrl = other.optionalCoverImageUrl().map(StringFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.city = other.optionalCity().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(BigDecimalFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(BigDecimalFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.websiteUrl = other.optionalWebsiteUrl().map(StringFilter::copy).orElse(null);
        this.instagramUrl = other.optionalInstagramUrl().map(StringFilter::copy).orElse(null);
        this.isFeatured = other.optionalIsFeatured().map(BooleanFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.offersId = other.optionalOffersId().map(LongFilter::copy).orElse(null);
        this.staffId = other.optionalStaffId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PartnerVenueCriteria copy() {
        return new PartnerVenueCriteria(this);
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

    public PartnerVenueCategoryFilter getCategory() {
        return category;
    }

    public Optional<PartnerVenueCategoryFilter> optionalCategory() {
        return Optional.ofNullable(category);
    }

    public PartnerVenueCategoryFilter category() {
        if (category == null) {
            setCategory(new PartnerVenueCategoryFilter());
        }
        return category;
    }

    public void setCategory(PartnerVenueCategoryFilter category) {
        this.category = category;
    }

    public StringFilter getLogoUrl() {
        return logoUrl;
    }

    public Optional<StringFilter> optionalLogoUrl() {
        return Optional.ofNullable(logoUrl);
    }

    public StringFilter logoUrl() {
        if (logoUrl == null) {
            setLogoUrl(new StringFilter());
        }
        return logoUrl;
    }

    public void setLogoUrl(StringFilter logoUrl) {
        this.logoUrl = logoUrl;
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

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
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

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public StringFilter getWebsiteUrl() {
        return websiteUrl;
    }

    public Optional<StringFilter> optionalWebsiteUrl() {
        return Optional.ofNullable(websiteUrl);
    }

    public StringFilter websiteUrl() {
        if (websiteUrl == null) {
            setWebsiteUrl(new StringFilter());
        }
        return websiteUrl;
    }

    public void setWebsiteUrl(StringFilter websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public StringFilter getInstagramUrl() {
        return instagramUrl;
    }

    public Optional<StringFilter> optionalInstagramUrl() {
        return Optional.ofNullable(instagramUrl);
    }

    public StringFilter instagramUrl() {
        if (instagramUrl == null) {
            setInstagramUrl(new StringFilter());
        }
        return instagramUrl;
    }

    public void setInstagramUrl(StringFilter instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public BooleanFilter getIsFeatured() {
        return isFeatured;
    }

    public Optional<BooleanFilter> optionalIsFeatured() {
        return Optional.ofNullable(isFeatured);
    }

    public BooleanFilter isFeatured() {
        if (isFeatured == null) {
            setIsFeatured(new BooleanFilter());
        }
        return isFeatured;
    }

    public void setIsFeatured(BooleanFilter isFeatured) {
        this.isFeatured = isFeatured;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
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

    public LongFilter getOffersId() {
        return offersId;
    }

    public Optional<LongFilter> optionalOffersId() {
        return Optional.ofNullable(offersId);
    }

    public LongFilter offersId() {
        if (offersId == null) {
            setOffersId(new LongFilter());
        }
        return offersId;
    }

    public void setOffersId(LongFilter offersId) {
        this.offersId = offersId;
    }

    public LongFilter getStaffId() {
        return staffId;
    }

    public Optional<LongFilter> optionalStaffId() {
        return Optional.ofNullable(staffId);
    }

    public LongFilter staffId() {
        if (staffId == null) {
            setStaffId(new LongFilter());
        }
        return staffId;
    }

    public void setStaffId(LongFilter staffId) {
        this.staffId = staffId;
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
        final PartnerVenueCriteria that = (PartnerVenueCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(slug, that.slug) &&
            Objects.equals(category, that.category) &&
            Objects.equals(logoUrl, that.logoUrl) &&
            Objects.equals(coverImageUrl, that.coverImageUrl) &&
            Objects.equals(address, that.address) &&
            Objects.equals(city, that.city) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(websiteUrl, that.websiteUrl) &&
            Objects.equals(instagramUrl, that.instagramUrl) &&
            Objects.equals(isFeatured, that.isFeatured) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(offersId, that.offersId) &&
            Objects.equals(staffId, that.staffId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            slug,
            category,
            logoUrl,
            coverImageUrl,
            address,
            city,
            latitude,
            longitude,
            phone,
            websiteUrl,
            instagramUrl,
            isFeatured,
            isActive,
            createdAt,
            updatedAt,
            offersId,
            staffId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerVenueCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalSlug().map(f -> "slug=" + f + ", ").orElse("") +
            optionalCategory().map(f -> "category=" + f + ", ").orElse("") +
            optionalLogoUrl().map(f -> "logoUrl=" + f + ", ").orElse("") +
            optionalCoverImageUrl().map(f -> "coverImageUrl=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalCity().map(f -> "city=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalWebsiteUrl().map(f -> "websiteUrl=" + f + ", ").orElse("") +
            optionalInstagramUrl().map(f -> "instagramUrl=" + f + ", ").orElse("") +
            optionalIsFeatured().map(f -> "isFeatured=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalOffersId().map(f -> "offersId=" + f + ", ").orElse("") +
            optionalStaffId().map(f -> "staffId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
