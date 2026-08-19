package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.TicketOfferAvailability;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;

@StaticMetamodel(CityEventTicketOffer.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CityEventTicketOffer_ {

	public static final String PROVIDER_CODE = "providerCode";
	public static final String RAW_PAYLOAD = "rawPayload";
	public static final String SELLER_NAME = "sellerName";
	public static final String AVAILABILITY = "availability";
	public static final String CITY_EVENT = "cityEvent";
	public static final String IS_OFFICIAL = "isOfficial";
	public static final String PURCHASE_URL = "purchaseUrl";
	public static final String LAST_CHECKED_AT = "lastCheckedAt";
	public static final String CREATED_AT = "createdAt";
	public static final String MIN_PRICE = "minPrice";
	public static final String CURRENCY = "currency";
	public static final String ID = "id";
	public static final String EXTERNAL_OFFER_ID = "externalOfferId";
	public static final String MAX_PRICE = "maxPrice";
	public static final String FEES_INCLUDED = "feesIncluded";
	public static final String PRICE_LABEL = "priceLabel";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#providerCode
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> providerCode;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#rawPayload
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> rawPayload;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#sellerName
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> sellerName;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#availability
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, TicketOfferAvailability> availability;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#cityEvent
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, CityEvent> cityEvent;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#isOfficial
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, Boolean> isOfficial;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#purchaseUrl
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> purchaseUrl;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#lastCheckedAt
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, Instant> lastCheckedAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#createdAt
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#minPrice
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, BigDecimal> minPrice;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#currency
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> currency;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#id
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#externalOfferId
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> externalOfferId;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#maxPrice
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, BigDecimal> maxPrice;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#feesIncluded
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, Boolean> feesIncluded;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer
	 **/
	public static volatile EntityType<CityEventTicketOffer> class_;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#priceLabel
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, String> priceLabel;
	
	/**
	 * @see com.bialem.backend.domain.CityEventTicketOffer#updatedAt
	 **/
	public static volatile SingularAttribute<CityEventTicketOffer, Instant> updatedAt;

}

