package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.CityEventStatus;
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
 * A CityEvent.
 */
@Entity
@Table(name = "city_event")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEvent implements Serializable {

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
    @Size(max = 80)
    @Column(name = "category", length = 80, nullable = false)
    private String category;

    @NotNull
    @Size(max = 80)
    @Column(name = "city", length = 80, nullable = false)
    private String city;

    @Size(max = 200)
    @Column(name = "venue_name", length = 200)
    private String venueName;

    @Size(max = 500)
    @Column(name = "address_text", length = 500)
    private String addressText;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Size(max = 2048)
    @Column(name = "cover_image_url", length = 2048)
    private String coverImageUrl;

    @Size(max = 120)
    @Column(name = "price_label", length = 120)
    private String priceLabel;

    @NotNull
    @Size(max = 120)
    @Column(name = "source_name", length = 120, nullable = false)
    private String sourceName;

    @Size(max = 2048)
    @Column(name = "source_url", length = 2048)
    private String sourceUrl;

    @Size(max = 2048)
    @Column(name = "ticket_url", length = 2048)
    private String ticketUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CityEventStatus status;

    @NotNull
    @Size(max = 50)
    @Column(name = "provider_code", length = 50, nullable = false)
    private String providerCode;

    @Size(max = 120)
    @Column(name = "external_id", length = 120)
    private String externalId;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "raw_payload")
    private String rawPayload;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cityEvent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cityEvent", "user" }, allowSetters = true)
    private Set<CityEventInterest> interests = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cityEvent")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "cityEvent" }, allowSetters = true)
    private Set<CityEventTicketOffer> ticketOffers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CityEvent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public CityEvent title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public CityEvent description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public CityEvent category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCity() {
        return this.city;
    }

    public CityEvent city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVenueName() {
        return this.venueName;
    }

    public CityEvent venueName(String venueName) {
        this.setVenueName(venueName);
        return this;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddressText() {
        return this.addressText;
    }

    public CityEvent addressText(String addressText) {
        this.setAddressText(addressText);
        return this;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    public Instant getStartsAt() {
        return this.startsAt;
    }

    public CityEvent startsAt(Instant startsAt) {
        this.setStartsAt(startsAt);
        return this;
    }

    public void setStartsAt(Instant startsAt) {
        this.startsAt = startsAt;
    }

    public Instant getEndsAt() {
        return this.endsAt;
    }

    public CityEvent endsAt(Instant endsAt) {
        this.setEndsAt(endsAt);
        return this;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public String getCoverImageUrl() {
        return this.coverImageUrl;
    }

    public CityEvent coverImageUrl(String coverImageUrl) {
        this.setCoverImageUrl(coverImageUrl);
        return this;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getPriceLabel() {
        return this.priceLabel;
    }

    public CityEvent priceLabel(String priceLabel) {
        this.setPriceLabel(priceLabel);
        return this;
    }

    public void setPriceLabel(String priceLabel) {
        this.priceLabel = priceLabel;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public CityEvent sourceName(String sourceName) {
        this.setSourceName(sourceName);
        return this;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public CityEvent sourceUrl(String sourceUrl) {
        this.setSourceUrl(sourceUrl);
        return this;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTicketUrl() {
        return this.ticketUrl;
    }

    public CityEvent ticketUrl(String ticketUrl) {
        this.setTicketUrl(ticketUrl);
        return this;
    }

    public void setTicketUrl(String ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public CityEventStatus getStatus() {
        return this.status;
    }

    public CityEvent status(CityEventStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CityEventStatus status) {
        this.status = status;
    }

    public String getProviderCode() {
        return this.providerCode;
    }

    public CityEvent providerCode(String providerCode) {
        this.setProviderCode(providerCode);
        return this;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public CityEvent externalId(String externalId) {
        this.setExternalId(externalId);
        return this;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Instant getLastSyncedAt() {
        return this.lastSyncedAt;
    }

    public CityEvent lastSyncedAt(Instant lastSyncedAt) {
        this.setLastSyncedAt(lastSyncedAt);
        return this;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }

    public String getRawPayload() {
        return this.rawPayload;
    }

    public CityEvent rawPayload(String rawPayload) {
        this.setRawPayload(rawPayload);
        return this;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public CityEvent createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CityEvent updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<CityEventInterest> getInterests() {
        return this.interests;
    }

    public void setInterests(Set<CityEventInterest> cityEventInterests) {
        if (this.interests != null) {
            this.interests.forEach(i -> i.setCityEvent(null));
        }
        if (cityEventInterests != null) {
            cityEventInterests.forEach(i -> i.setCityEvent(this));
        }
        this.interests = cityEventInterests;
    }

    public CityEvent interests(Set<CityEventInterest> cityEventInterests) {
        this.setInterests(cityEventInterests);
        return this;
    }

    public CityEvent addInterests(CityEventInterest cityEventInterest) {
        this.interests.add(cityEventInterest);
        cityEventInterest.setCityEvent(this);
        return this;
    }

    public CityEvent removeInterests(CityEventInterest cityEventInterest) {
        this.interests.remove(cityEventInterest);
        cityEventInterest.setCityEvent(null);
        return this;
    }

    public Set<CityEventTicketOffer> getTicketOffers() {
        return this.ticketOffers;
    }

    public void setTicketOffers(Set<CityEventTicketOffer> cityEventTicketOffers) {
        if (this.ticketOffers != null) {
            this.ticketOffers.forEach(i -> i.setCityEvent(null));
        }
        if (cityEventTicketOffers != null) {
            cityEventTicketOffers.forEach(i -> i.setCityEvent(this));
        }
        this.ticketOffers = cityEventTicketOffers;
    }

    public CityEvent ticketOffers(Set<CityEventTicketOffer> cityEventTicketOffers) {
        this.setTicketOffers(cityEventTicketOffers);
        return this;
    }

    public CityEvent addTicketOffers(CityEventTicketOffer cityEventTicketOffer) {
        this.ticketOffers.add(cityEventTicketOffer);
        cityEventTicketOffer.setCityEvent(this);
        return this;
    }

    public CityEvent removeTicketOffers(CityEventTicketOffer cityEventTicketOffer) {
        this.ticketOffers.remove(cityEventTicketOffer);
        cityEventTicketOffer.setCityEvent(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityEvent)) {
            return false;
        }
        return getId() != null && getId().equals(((CityEvent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEvent{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", city='" + getCity() + "'" +
            ", venueName='" + getVenueName() + "'" +
            ", addressText='" + getAddressText() + "'" +
            ", startsAt='" + getStartsAt() + "'" +
            ", endsAt='" + getEndsAt() + "'" +
            ", coverImageUrl='" + getCoverImageUrl() + "'" +
            ", priceLabel='" + getPriceLabel() + "'" +
            ", sourceName='" + getSourceName() + "'" +
            ", sourceUrl='" + getSourceUrl() + "'" +
            ", ticketUrl='" + getTicketUrl() + "'" +
            ", status='" + getStatus() + "'" +
            ", providerCode='" + getProviderCode() + "'" +
            ", externalId='" + getExternalId() + "'" +
            ", lastSyncedAt='" + getLastSyncedAt() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
