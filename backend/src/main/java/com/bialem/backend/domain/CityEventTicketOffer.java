package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.TicketOfferAvailability;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * A CityEventTicketOffer.
 */
@Entity
@Table(name = "city_event_ticket_offer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CityEventTicketOffer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "provider_code", length = 50, nullable = false)
    private String providerCode;

    @NotNull
    @Size(max = 120)
    @Column(name = "external_offer_id", length = 120, nullable = false)
    private String externalOfferId;

    @NotNull
    @Size(max = 160)
    @Column(name = "seller_name", length = 160, nullable = false)
    private String sellerName;

    @NotNull
    @Size(max = 2048)
    @Column(name = "purchase_url", length = 2048, nullable = false)
    private String purchaseUrl;

    @Size(max = 8)
    @Column(name = "currency", length = 8)
    private String currency;

    @DecimalMin(value = "0")
    @Column(name = "min_price", precision = 21, scale = 2)
    private BigDecimal minPrice;

    @DecimalMin(value = "0")
    @Column(name = "max_price", precision = 21, scale = 2)
    private BigDecimal maxPrice;

    @Size(max = 120)
    @Column(name = "price_label", length = 120)
    private String priceLabel;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "availability", nullable = false)
    private TicketOfferAvailability availability;

    @Column(name = "fees_included")
    private Boolean feesIncluded;

    @NotNull
    @Column(name = "is_official", nullable = false)
    private Boolean isOfficial;

    @NotNull
    @Column(name = "last_checked_at", nullable = false)
    private Instant lastCheckedAt;

    @JdbcTypeCode(SqlTypes.LONG32VARCHAR)
    @Column(name = "raw_payload")
    private String rawPayload;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "interests", "ticketOffers" }, allowSetters = true)
    private CityEvent cityEvent;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CityEventTicketOffer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderCode() {
        return this.providerCode;
    }

    public CityEventTicketOffer providerCode(String providerCode) {
        this.setProviderCode(providerCode);
        return this;
    }

    public void setProviderCode(String providerCode) {
        this.providerCode = providerCode;
    }

    public String getExternalOfferId() {
        return this.externalOfferId;
    }

    public CityEventTicketOffer externalOfferId(String externalOfferId) {
        this.setExternalOfferId(externalOfferId);
        return this;
    }

    public void setExternalOfferId(String externalOfferId) {
        this.externalOfferId = externalOfferId;
    }

    public String getSellerName() {
        return this.sellerName;
    }

    public CityEventTicketOffer sellerName(String sellerName) {
        this.setSellerName(sellerName);
        return this;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getPurchaseUrl() {
        return this.purchaseUrl;
    }

    public CityEventTicketOffer purchaseUrl(String purchaseUrl) {
        this.setPurchaseUrl(purchaseUrl);
        return this;
    }

    public void setPurchaseUrl(String purchaseUrl) {
        this.purchaseUrl = purchaseUrl;
    }

    public String getCurrency() {
        return this.currency;
    }

    public CityEventTicketOffer currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getMinPrice() {
        return this.minPrice;
    }

    public CityEventTicketOffer minPrice(BigDecimal minPrice) {
        this.setMinPrice(minPrice);
        return this;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return this.maxPrice;
    }

    public CityEventTicketOffer maxPrice(BigDecimal maxPrice) {
        this.setMaxPrice(maxPrice);
        return this;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getPriceLabel() {
        return this.priceLabel;
    }

    public CityEventTicketOffer priceLabel(String priceLabel) {
        this.setPriceLabel(priceLabel);
        return this;
    }

    public void setPriceLabel(String priceLabel) {
        this.priceLabel = priceLabel;
    }

    public TicketOfferAvailability getAvailability() {
        return this.availability;
    }

    public CityEventTicketOffer availability(TicketOfferAvailability availability) {
        this.setAvailability(availability);
        return this;
    }

    public void setAvailability(TicketOfferAvailability availability) {
        this.availability = availability;
    }

    public Boolean getFeesIncluded() {
        return this.feesIncluded;
    }

    public CityEventTicketOffer feesIncluded(Boolean feesIncluded) {
        this.setFeesIncluded(feesIncluded);
        return this;
    }

    public void setFeesIncluded(Boolean feesIncluded) {
        this.feesIncluded = feesIncluded;
    }

    public Boolean getIsOfficial() {
        return this.isOfficial;
    }

    public CityEventTicketOffer isOfficial(Boolean isOfficial) {
        this.setIsOfficial(isOfficial);
        return this;
    }

    public void setIsOfficial(Boolean isOfficial) {
        this.isOfficial = isOfficial;
    }

    public Instant getLastCheckedAt() {
        return this.lastCheckedAt;
    }

    public CityEventTicketOffer lastCheckedAt(Instant lastCheckedAt) {
        this.setLastCheckedAt(lastCheckedAt);
        return this;
    }

    public void setLastCheckedAt(Instant lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public String getRawPayload() {
        return this.rawPayload;
    }

    public CityEventTicketOffer rawPayload(String rawPayload) {
        this.setRawPayload(rawPayload);
        return this;
    }

    public void setRawPayload(String rawPayload) {
        this.rawPayload = rawPayload;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public CityEventTicketOffer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public CityEventTicketOffer updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CityEvent getCityEvent() {
        return this.cityEvent;
    }

    public void setCityEvent(CityEvent cityEvent) {
        this.cityEvent = cityEvent;
    }

    public CityEventTicketOffer cityEvent(CityEvent cityEvent) {
        this.setCityEvent(cityEvent);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CityEventTicketOffer)) {
            return false;
        }
        return getId() != null && getId().equals(((CityEventTicketOffer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CityEventTicketOffer{" +
            "id=" + getId() +
            ", providerCode='" + getProviderCode() + "'" +
            ", externalOfferId='" + getExternalOfferId() + "'" +
            ", sellerName='" + getSellerName() + "'" +
            ", purchaseUrl='" + getPurchaseUrl() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", minPrice=" + getMinPrice() +
            ", maxPrice=" + getMaxPrice() +
            ", priceLabel='" + getPriceLabel() + "'" +
            ", availability='" + getAvailability() + "'" +
            ", feesIncluded='" + getFeesIncluded() + "'" +
            ", isOfficial='" + getIsOfficial() + "'" +
            ", lastCheckedAt='" + getLastCheckedAt() + "'" +
            ", rawPayload='" + getRawPayload() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
