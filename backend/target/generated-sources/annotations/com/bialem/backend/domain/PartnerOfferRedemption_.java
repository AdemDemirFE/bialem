package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.RedemptionStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@StaticMetamodel(PartnerOfferRedemption.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PartnerOfferRedemption_ {

	public static final String REDEMPTION_CODE = "redemptionCode";
	public static final String VENUE = "venue";
	public static final String REDEEMED_AT = "redeemedAt";
	public static final String DISCOUNT_AMOUNT = "discountAmount";
	public static final String EXPIRES_AT = "expiresAt";
	public static final String TOKEN = "token";
	public static final String OFFER = "offer";
	public static final String ORDER_AMOUNT = "orderAmount";
	public static final String REDEEMED_BY = "redeemedBy";
	public static final String ID = "id";
	public static final String ISSUED_AT = "issuedAt";
	public static final String USER = "user";
	public static final String STATUS = "status";

	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#redemptionCode
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, String> redemptionCode;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#venue
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, PartnerVenue> venue;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#redeemedAt
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, Instant> redeemedAt;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#discountAmount
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, BigDecimal> discountAmount;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#expiresAt
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, Instant> expiresAt;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#token
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, UUID> token;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#offer
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, PartnerOffer> offer;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#orderAmount
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, BigDecimal> orderAmount;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#redeemedBy
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, Profile> redeemedBy;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#id
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#issuedAt
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, Instant> issuedAt;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption
	 **/
	public static volatile EntityType<PartnerOfferRedemption> class_;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#user
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOfferRedemption#status
	 **/
	public static volatile SingularAttribute<PartnerOfferRedemption, RedemptionStatus> status;

}

