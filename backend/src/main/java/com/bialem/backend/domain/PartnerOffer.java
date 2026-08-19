package com.bialem.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A PartnerOffer.
 */
@Entity
@Table(name = "partner_offer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerOffer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 160)
    @Column(name = "title", length = 160, nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    @Column(name = "discount_percent", precision = 21, scale = 2, nullable = false)
    private BigDecimal discountPercent;

    @DecimalMin(value = "0")
    @Column(name = "minimum_spend", precision = 21, scale = 2)
    private BigDecimal minimumSpend;

    @DecimalMin(value = "0")
    @Column(name = "maximum_discount", precision = 21, scale = 2)
    private BigDecimal maximumDiscount;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @Column(name = "valid_until")
    private Instant validUntil;

    @Size(max = 32)
    @Column(name = "valid_days", length = 32)
    private String validDays;

    @Column(name = "daily_start_time")
    private LocalTime dailyStartTime;

    @Column(name = "daily_end_time")
    private LocalTime dailyEndTime;

    @Min(value = 1)
    @Column(name = "per_user_limit")
    private Integer perUserLimit;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "terms")
    private String terms;

    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "offers", "staff" }, allowSetters = true)
    private PartnerVenue venue;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "offer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "offer", "venue", "user", "redeemedBy" }, allowSetters = true)
    private Set<PartnerOfferRedemption> redemptions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PartnerOffer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public PartnerOffer title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public PartnerOffer description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountPercent() {
        return this.discountPercent;
    }

    public PartnerOffer discountPercent(BigDecimal discountPercent) {
        this.setDiscountPercent(discountPercent);
        return this;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getMinimumSpend() {
        return this.minimumSpend;
    }

    public PartnerOffer minimumSpend(BigDecimal minimumSpend) {
        this.setMinimumSpend(minimumSpend);
        return this;
    }

    public void setMinimumSpend(BigDecimal minimumSpend) {
        this.minimumSpend = minimumSpend;
    }

    public BigDecimal getMaximumDiscount() {
        return this.maximumDiscount;
    }

    public PartnerOffer maximumDiscount(BigDecimal maximumDiscount) {
        this.setMaximumDiscount(maximumDiscount);
        return this;
    }

    public void setMaximumDiscount(BigDecimal maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }

    public Instant getValidFrom() {
        return this.validFrom;
    }

    public PartnerOffer validFrom(Instant validFrom) {
        this.setValidFrom(validFrom);
        return this;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return this.validUntil;
    }

    public PartnerOffer validUntil(Instant validUntil) {
        this.setValidUntil(validUntil);
        return this;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public String getValidDays() {
        return this.validDays;
    }

    public PartnerOffer validDays(String validDays) {
        this.setValidDays(validDays);
        return this;
    }

    public void setValidDays(String validDays) {
        this.validDays = validDays;
    }

    public LocalTime getDailyStartTime() {
        return this.dailyStartTime;
    }

    public PartnerOffer dailyStartTime(LocalTime dailyStartTime) {
        this.setDailyStartTime(dailyStartTime);
        return this;
    }

    public void setDailyStartTime(LocalTime dailyStartTime) {
        this.dailyStartTime = dailyStartTime;
    }

    public LocalTime getDailyEndTime() {
        return this.dailyEndTime;
    }

    public PartnerOffer dailyEndTime(LocalTime dailyEndTime) {
        this.setDailyEndTime(dailyEndTime);
        return this;
    }

    public void setDailyEndTime(LocalTime dailyEndTime) {
        this.dailyEndTime = dailyEndTime;
    }

    public Integer getPerUserLimit() {
        return this.perUserLimit;
    }

    public PartnerOffer perUserLimit(Integer perUserLimit) {
        this.setPerUserLimit(perUserLimit);
        return this;
    }

    public void setPerUserLimit(Integer perUserLimit) {
        this.perUserLimit = perUserLimit;
    }

    public String getTerms() {
        return this.terms;
    }

    public PartnerOffer terms(String terms) {
        this.setTerms(terms);
        return this;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public PartnerOffer isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public PartnerOffer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public PartnerOffer updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PartnerVenue getVenue() {
        return this.venue;
    }

    public void setVenue(PartnerVenue partnerVenue) {
        this.venue = partnerVenue;
    }

    public PartnerOffer venue(PartnerVenue partnerVenue) {
        this.setVenue(partnerVenue);
        return this;
    }

    public Set<PartnerOfferRedemption> getRedemptions() {
        return this.redemptions;
    }

    public void setRedemptions(Set<PartnerOfferRedemption> partnerOfferRedemptions) {
        if (this.redemptions != null) {
            this.redemptions.forEach(i -> i.setOffer(null));
        }
        if (partnerOfferRedemptions != null) {
            partnerOfferRedemptions.forEach(i -> i.setOffer(this));
        }
        this.redemptions = partnerOfferRedemptions;
    }

    public PartnerOffer redemptions(Set<PartnerOfferRedemption> partnerOfferRedemptions) {
        this.setRedemptions(partnerOfferRedemptions);
        return this;
    }

    public PartnerOffer addRedemptions(PartnerOfferRedemption partnerOfferRedemption) {
        this.redemptions.add(partnerOfferRedemption);
        partnerOfferRedemption.setOffer(this);
        return this;
    }

    public PartnerOffer removeRedemptions(PartnerOfferRedemption partnerOfferRedemption) {
        this.redemptions.remove(partnerOfferRedemption);
        partnerOfferRedemption.setOffer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerOffer)) {
            return false;
        }
        return getId() != null && getId().equals(((PartnerOffer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerOffer{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", discountPercent=" + getDiscountPercent() +
            ", minimumSpend=" + getMinimumSpend() +
            ", maximumDiscount=" + getMaximumDiscount() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", validDays='" + getValidDays() + "'" +
            ", dailyStartTime='" + getDailyStartTime() + "'" +
            ", dailyEndTime='" + getDailyEndTime() + "'" +
            ", perUserLimit=" + getPerUserLimit() +
            ", terms='" + getTerms() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
