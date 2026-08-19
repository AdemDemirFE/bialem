package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;

@StaticMetamodel(PartnerOffer.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PartnerOffer_ {

	public static final String MINIMUM_SPEND = "minimumSpend";
	public static final String VENUE = "venue";
	public static final String DISCOUNT_PERCENT = "discountPercent";
	public static final String DAILY_START_TIME = "dailyStartTime";
	public static final String DESCRIPTION = "description";
	public static final String VALID_FROM = "validFrom";
	public static final String DAILY_END_TIME = "dailyEndTime";
	public static final String TITLE = "title";
	public static final String IS_ACTIVE = "isActive";
	public static final String CREATED_AT = "createdAt";
	public static final String MAXIMUM_DISCOUNT = "maximumDiscount";
	public static final String TERMS = "terms";
	public static final String REDEMPTIONS = "redemptions";
	public static final String PER_USER_LIMIT = "perUserLimit";
	public static final String VALID_UNTIL = "validUntil";
	public static final String ID = "id";
	public static final String VALID_DAYS = "validDays";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#minimumSpend
	 **/
	public static volatile SingularAttribute<PartnerOffer, BigDecimal> minimumSpend;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#venue
	 **/
	public static volatile SingularAttribute<PartnerOffer, PartnerVenue> venue;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#discountPercent
	 **/
	public static volatile SingularAttribute<PartnerOffer, BigDecimal> discountPercent;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#dailyStartTime
	 **/
	public static volatile SingularAttribute<PartnerOffer, LocalTime> dailyStartTime;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#description
	 **/
	public static volatile SingularAttribute<PartnerOffer, String> description;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#validFrom
	 **/
	public static volatile SingularAttribute<PartnerOffer, Instant> validFrom;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#dailyEndTime
	 **/
	public static volatile SingularAttribute<PartnerOffer, LocalTime> dailyEndTime;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#title
	 **/
	public static volatile SingularAttribute<PartnerOffer, String> title;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#isActive
	 **/
	public static volatile SingularAttribute<PartnerOffer, Boolean> isActive;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#createdAt
	 **/
	public static volatile SingularAttribute<PartnerOffer, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#maximumDiscount
	 **/
	public static volatile SingularAttribute<PartnerOffer, BigDecimal> maximumDiscount;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#terms
	 **/
	public static volatile SingularAttribute<PartnerOffer, String> terms;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#redemptions
	 **/
	public static volatile SetAttribute<PartnerOffer, PartnerOfferRedemption> redemptions;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#perUserLimit
	 **/
	public static volatile SingularAttribute<PartnerOffer, Integer> perUserLimit;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#validUntil
	 **/
	public static volatile SingularAttribute<PartnerOffer, Instant> validUntil;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#id
	 **/
	public static volatile SingularAttribute<PartnerOffer, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer
	 **/
	public static volatile EntityType<PartnerOffer> class_;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#validDays
	 **/
	public static volatile SingularAttribute<PartnerOffer, String> validDays;
	
	/**
	 * @see com.bialem.backend.domain.PartnerOffer#updatedAt
	 **/
	public static volatile SingularAttribute<PartnerOffer, Instant> updatedAt;

}

