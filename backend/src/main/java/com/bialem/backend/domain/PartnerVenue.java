package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PartnerVenueCategory;
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
 * A PartnerVenue.
 */
@Entity
@Table(name = "partner_venue")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerVenue implements Serializable {

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
    @Column(name = "category", nullable = false)
    private PartnerVenueCategory category;

    @Size(max = 2048)
    @Column(name = "logo_url", length = 2048)
    private String logoUrl;

    @Size(max = 2048)
    @Column(name = "cover_image_url", length = 2048)
    private String coverImageUrl;

    @NotNull
    @Size(max = 500)
    @Column(name = "address", length = 500, nullable = false)
    private String address;

    @NotNull
    @Size(max = 80)
    @Column(name = "city", length = 80, nullable = false)
    private String city;

    @Column(name = "latitude", precision = 21, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 21, scale = 2)
    private BigDecimal longitude;

    @Size(max = 40)
    @Column(name = "phone", length = 40)
    private String phone;

    @Size(max = 2048)
    @Column(name = "website_url", length = 2048)
    private String websiteUrl;

    @Size(max = 2048)
    @Column(name = "instagram_url", length = 2048)
    private String instagramUrl;

    @NotNull
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venue")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "venue", "redemptions" }, allowSetters = true)
    private Set<PartnerOffer> offers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "venue")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "venue", "user" }, allowSetters = true)
    private Set<PartnerVenueStaff> staff = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PartnerVenue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public PartnerVenue name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return this.slug;
    }

    public PartnerVenue slug(String slug) {
        this.setSlug(slug);
        return this;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return this.description;
    }

    public PartnerVenue description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PartnerVenueCategory getCategory() {
        return this.category;
    }

    public PartnerVenue category(PartnerVenueCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(PartnerVenueCategory category) {
        this.category = category;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public PartnerVenue logoUrl(String logoUrl) {
        this.setLogoUrl(logoUrl);
        return this;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public PartnerVenue coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getAddress() {
        return this.address;
    }

    public PartnerVenue address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return this.city;
    }

    public PartnerVenue city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public PartnerVenue latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public PartnerVenue longitude(BigDecimal longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return this.phone;
    }

    public PartnerVenue phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }

    public PartnerVenue websiteUrl(String websiteUrl) {
        this.setWebsiteUrl(websiteUrl);
        return this;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getInstagramUrl() {
        return this.instagramUrl;
    }

    public PartnerVenue instagramUrl(String instagramUrl) {
        this.setInstagramUrl(instagramUrl);
        return this;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public Boolean getIsFeatured() {
        return this.isFeatured;
    }

    public PartnerVenue isFeatured(Boolean isFeatured) {
        this.setIsFeatured(isFeatured);
        return this;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public PartnerVenue isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PartnerVenue createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PartnerVenue updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<PartnerOffer> getOffers() {
        return this.offers;
    }

    public void setOffers(Set<PartnerOffer> partnerOffers) {
        if (this.offers != null) {
            this.offers.forEach(i -> i.setVenue(null));
        }
        if (partnerOffers != null) {
            partnerOffers.forEach(i -> i.setVenue(this));
        }
        this.offers = partnerOffers;
    }

    public PartnerVenue offers(Set<PartnerOffer> partnerOffers) {
        this.setOffers(partnerOffers);
        return this;
    }

    public PartnerVenue addOffers(PartnerOffer partnerOffer) {
        this.offers.add(partnerOffer);
        partnerOffer.setVenue(this);
        return this;
    }

    public PartnerVenue removeOffers(PartnerOffer partnerOffer) {
        this.offers.remove(partnerOffer);
        partnerOffer.setVenue(null);
        return this;
    }

    public Set<PartnerVenueStaff> getStaff() {
        return this.staff;
    }

    public void setStaff(Set<PartnerVenueStaff> partnerVenueStaffs) {
        if (this.staff != null) {
            this.staff.forEach(i -> i.setVenue(null));
        }
        if (partnerVenueStaffs != null) {
            partnerVenueStaffs.forEach(i -> i.setVenue(this));
        }
        this.staff = partnerVenueStaffs;
    }

    public PartnerVenue staff(Set<PartnerVenueStaff> partnerVenueStaffs) {
        this.setStaff(partnerVenueStaffs);
        return this;
    }

    public PartnerVenue addStaff(PartnerVenueStaff partnerVenueStaff) {
        this.staff.add(partnerVenueStaff);
        partnerVenueStaff.setVenue(this);
        return this;
    }

    public PartnerVenue removeStaff(PartnerVenueStaff partnerVenueStaff) {
        this.staff.remove(partnerVenueStaff);
        partnerVenueStaff.setVenue(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerVenue)) {
            return false;
        }
        return getId() != null && getId().equals(((PartnerVenue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerVenue{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slug='" + getSlug() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", logoUrl='" + getLogoUrl() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", address='" + getAddress() + "'" +
            ", city='" + getCity() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", phone='" + getPhone() + "'" +
            ", websiteUrl='" + getWebsiteUrl() + "'" +
            ", instagramUrl='" + getInstagramUrl() + "'" +
            ", isFeatured='" + getIsFeatured() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
