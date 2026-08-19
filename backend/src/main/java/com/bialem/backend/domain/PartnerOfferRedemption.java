package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.RedemptionStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PartnerOfferRedemption.
 */
@Entity
@Table(name = "partner_offer_redemption")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PartnerOfferRedemption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "token", nullable = false, unique = true)
    private UUID token;

    @NotNull
    @Size(max = 32)
    @Column(name = "redemption_code", length = 32, nullable = false, unique = true)
    private String redemptionCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RedemptionStatus status;

    @NotNull
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @NotNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "redeemed_at")
    private Instant redeemedAt;

    @DecimalMin(value = "0")
    @Column(name = "order_amount", precision = 21, scale = 2)
    private BigDecimal orderAmount;

    @DecimalMin(value = "0")
    @Column(name = "discount_amount", precision = 21, scale = 2)
    private BigDecimal discountAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "venue", "redemptions" }, allowSetters = true)
    private PartnerOffer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "offers", "staff" }, allowSetters = true)
    private PartnerVenue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "preferences" }, allowSetters = true)
    private Profile redeemedBy;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PartnerOfferRedemption id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getToken() {
        return this.token;
    }

    public PartnerOfferRedemption token(UUID token) {
        this.setToken(token);
        return this;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getRedemptionCode() {
        return this.redemptionCode;
    }

    public PartnerOfferRedemption redemptionCode(String redemptionCode) {
        this.setRedemptionCode(redemptionCode);
        return this;
    }

    public void setRedemptionCode(String redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    public RedemptionStatus getStatus() {
        return this.status;
    }

    public PartnerOfferRedemption status(RedemptionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RedemptionStatus status) {
        this.status = status;
    }

    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    public PartnerOfferRedemption issuedAt(Instant issuedAt) {
        this.setIssuedAt(issuedAt);
        return this;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Instant getExpiresAt() {
        return this.expiresAt;
    }

    public PartnerOfferRedemption expiresAt(Instant expiresAt) {
        this.setExpiresAt(expiresAt);
        return this;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRedeemedAt() {
        return this.redeemedAt;
    }

    public PartnerOfferRedemption redeemedAt(Instant redeemedAt) {
        this.setRedeemedAt(redeemedAt);
        return this;
    }

    public void setRedeemedAt(Instant redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public BigDecimal getOrderAmount() {
        return this.orderAmount;
    }

    public PartnerOfferRedemption orderAmount(BigDecimal orderAmount) {
        this.setOrderAmount(orderAmount);
        return this;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getDiscountAmount() {
        return this.discountAmount;
    }

    public PartnerOfferRedemption discountAmount(BigDecimal discountAmount) {
        this.setDiscountAmount(discountAmount);
        return this;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public PartnerOffer getOffer() {
        return this.offer;
    }

    public void setOffer(PartnerOffer partnerOffer) {
        this.offer = partnerOffer;
    }

    public PartnerOfferRedemption offer(PartnerOffer partnerOffer) {
        this.setOffer(partnerOffer);
        return this;
    }

    public PartnerVenue getVenue() {
        return this.venue;
    }

    public void setVenue(PartnerVenue partnerVenue) {
        this.venue = partnerVenue;
    }

    public PartnerOfferRedemption venue(PartnerVenue partnerVenue) {
        this.setVenue(partnerVenue);
        return this;
    }

    public Profile getUser() {
        return this.user;
    }

    public void setUser(Profile profile) {
        this.user = profile;
    }

    public PartnerOfferRedemption user(Profile profile) {
        this.setUser(profile);
        return this;
    }

    public Profile getRedeemedBy() {
        return this.redeemedBy;
    }

    public void setRedeemedBy(Profile profile) {
        this.redeemedBy = profile;
    }

    public PartnerOfferRedemption redeemedBy(Profile profile) {
        this.setRedeemedBy(profile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PartnerOfferRedemption)) {
            return false;
        }
        return getId() != null && getId().equals(((PartnerOfferRedemption) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PartnerOfferRedemption{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", redemptionCode='" + getRedemptionCode() + "'" +
            ", status='" + getStatus() + "'" +
            ", issuedAt='" + getIssuedAt() + "'" +
            ", expiresAt='" + getExpiresAt() + "'" +
            ", redeemedAt='" + getRedeemedAt() + "'" +
            ", orderAmount=" + getOrderAmount() +
            ", discountAmount=" + getDiscountAmount() +
            "}";
    }
}
