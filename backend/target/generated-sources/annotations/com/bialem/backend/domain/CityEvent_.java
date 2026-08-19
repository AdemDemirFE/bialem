package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.CityEventStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(CityEvent.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CityEvent_ {

	public static final String TICKET_URL = "ticketUrl";
	public static final String PROVIDER_CODE = "providerCode";
	public static final String RAW_PAYLOAD = "rawPayload";
	public static final String CITY = "city";
	public static final String COVER_IMAGE_URL = "coverImageUrl";
	public static final String LAST_SYNCED_AT = "lastSyncedAt";
	public static final String DESCRIPTION = "description";
	public static final String ADDRESS_TEXT = "addressText";
	public static final String EXTERNAL_ID = "externalId";
	public static final String TITLE = "title";
	public static final String VENUE_NAME = "venueName";
	public static final String SOURCE_URL = "sourceUrl";
	public static final String TICKET_OFFERS = "ticketOffers";
	public static final String CREATED_AT = "createdAt";
	public static final String STARTS_AT = "startsAt";
	public static final String ID = "id";
	public static final String SOURCE_NAME = "sourceName";
	public static final String CATEGORY = "category";
	public static final String INTERESTS = "interests";
	public static final String ENDS_AT = "endsAt";
	public static final String PRICE_LABEL = "priceLabel";
	public static final String STATUS = "status";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.CityEvent#ticketUrl
	 **/
	public static volatile SingularAttribute<CityEvent, String> ticketUrl;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#providerCode
	 **/
	public static volatile SingularAttribute<CityEvent, String> providerCode;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#rawPayload
	 **/
	public static volatile SingularAttribute<CityEvent, String> rawPayload;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#city
	 **/
	public static volatile SingularAttribute<CityEvent, String> city;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#coverImageUrl
	 **/
	public static volatile SingularAttribute<CityEvent, String> coverImageUrl;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#lastSyncedAt
	 **/
	public static volatile SingularAttribute<CityEvent, Instant> lastSyncedAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#description
	 **/
	public static volatile SingularAttribute<CityEvent, String> description;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#addressText
	 **/
	public static volatile SingularAttribute<CityEvent, String> addressText;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#externalId
	 **/
	public static volatile SingularAttribute<CityEvent, String> externalId;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#title
	 **/
	public static volatile SingularAttribute<CityEvent, String> title;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#venueName
	 **/
	public static volatile SingularAttribute<CityEvent, String> venueName;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#sourceUrl
	 **/
	public static volatile SingularAttribute<CityEvent, String> sourceUrl;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#ticketOffers
	 **/
	public static volatile SetAttribute<CityEvent, CityEventTicketOffer> ticketOffers;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#createdAt
	 **/
	public static volatile SingularAttribute<CityEvent, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#startsAt
	 **/
	public static volatile SingularAttribute<CityEvent, Instant> startsAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#id
	 **/
	public static volatile SingularAttribute<CityEvent, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#sourceName
	 **/
	public static volatile SingularAttribute<CityEvent, String> sourceName;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#category
	 **/
	public static volatile SingularAttribute<CityEvent, String> category;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#interests
	 **/
	public static volatile SetAttribute<CityEvent, CityEventInterest> interests;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent
	 **/
	public static volatile EntityType<CityEvent> class_;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#endsAt
	 **/
	public static volatile SingularAttribute<CityEvent, Instant> endsAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#priceLabel
	 **/
	public static volatile SingularAttribute<CityEvent, String> priceLabel;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#status
	 **/
	public static volatile SingularAttribute<CityEvent, CityEventStatus> status;
	
	/**
	 * @see com.bialem.backend.domain.CityEvent#updatedAt
	 **/
	public static volatile SingularAttribute<CityEvent, Instant> updatedAt;

}

