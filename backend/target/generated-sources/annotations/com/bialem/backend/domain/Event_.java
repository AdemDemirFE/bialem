package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.EventStatus;
import com.bialem.backend.domain.enumeration.GroupModerationStatus;
import com.bialem.backend.domain.enumeration.PlatformModerationStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;

@StaticMetamodel(Event.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Event_ {

	public static final String COVER_IMAGE_URL = "coverImageUrl";
	public static final String LATITUDE = "latitude";
	public static final String DESCRIPTION = "description";
	public static final String CANCELLED_BY = "cancelledBy";
	public static final String TITLE = "title";
	public static final String POSTS = "posts";
	public static final String CAPACITY = "capacity";
	public static final String CREATED_AT = "createdAt";
	public static final String RATINGS = "ratings";
	public static final String STARTS_AT = "startsAt";
	public static final String ID = "id";
	public static final String ENDS_AT = "endsAt";
	public static final String LONGITUDE = "longitude";
	public static final String UPDATED_AT = "updatedAt";
	public static final String PARTICIPANTS = "participants";
	public static final String LOCATION_NAME = "locationName";
	public static final String PUBLISHED_AT = "publishedAt";
	public static final String CANCELLATION_REASON = "cancellationReason";
	public static final String GROUP_MODERATION_STATUS = "groupModerationStatus";
	public static final String CANCELLED_AT = "cancelledAt";
	public static final String ADDRESS_TEXT = "addressText";
	public static final String COMMUNITY = "community";
	public static final String CREATED_BY = "createdBy";
	public static final String PUBLISHED_TO_DISCOVERY = "publishedToDiscovery";
	public static final String PLATFORM_MODERATION_STATUS = "platformModerationStatus";
	public static final String MESSAGES = "messages";
	public static final String REJECTION_REASON = "rejectionReason";
	public static final String CATEGORY = "category";
	public static final String STATUS = "status";

	
	/**
	 * @see com.bialem.backend.domain.Event#coverImageUrl
	 **/
	public static volatile SingularAttribute<Event, String> coverImageUrl;
	
	/**
	 * @see com.bialem.backend.domain.Event#latitude
	 **/
	public static volatile SingularAttribute<Event, BigDecimal> latitude;
	
	/**
	 * @see com.bialem.backend.domain.Event#description
	 **/
	public static volatile SingularAttribute<Event, String> description;
	
	/**
	 * @see com.bialem.backend.domain.Event#cancelledBy
	 **/
	public static volatile SingularAttribute<Event, Profile> cancelledBy;
	
	/**
	 * @see com.bialem.backend.domain.Event#title
	 **/
	public static volatile SingularAttribute<Event, String> title;
	
	/**
	 * @see com.bialem.backend.domain.Event#posts
	 **/
	public static volatile SetAttribute<Event, Post> posts;
	
	/**
	 * @see com.bialem.backend.domain.Event#capacity
	 **/
	public static volatile SingularAttribute<Event, Integer> capacity;
	
	/**
	 * @see com.bialem.backend.domain.Event#createdAt
	 **/
	public static volatile SingularAttribute<Event, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Event#ratings
	 **/
	public static volatile SetAttribute<Event, EventRating> ratings;
	
	/**
	 * @see com.bialem.backend.domain.Event#startsAt
	 **/
	public static volatile SingularAttribute<Event, Instant> startsAt;
	
	/**
	 * @see com.bialem.backend.domain.Event#id
	 **/
	public static volatile SingularAttribute<Event, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Event
	 **/
	public static volatile EntityType<Event> class_;
	
	/**
	 * @see com.bialem.backend.domain.Event#endsAt
	 **/
	public static volatile SingularAttribute<Event, Instant> endsAt;
	
	/**
	 * @see com.bialem.backend.domain.Event#longitude
	 **/
	public static volatile SingularAttribute<Event, BigDecimal> longitude;
	
	/**
	 * @see com.bialem.backend.domain.Event#updatedAt
	 **/
	public static volatile SingularAttribute<Event, Instant> updatedAt;
	
	/**
	 * @see com.bialem.backend.domain.Event#participants
	 **/
	public static volatile SetAttribute<Event, EventParticipant> participants;
	
	/**
	 * @see com.bialem.backend.domain.Event#locationName
	 **/
	public static volatile SingularAttribute<Event, String> locationName;
	
	/**
	 * @see com.bialem.backend.domain.Event#publishedAt
	 **/
	public static volatile SingularAttribute<Event, Instant> publishedAt;
	
	/**
	 * @see com.bialem.backend.domain.Event#cancellationReason
	 **/
	public static volatile SingularAttribute<Event, String> cancellationReason;
	
	/**
	 * @see com.bialem.backend.domain.Event#groupModerationStatus
	 **/
	public static volatile SingularAttribute<Event, GroupModerationStatus> groupModerationStatus;
	
	/**
	 * @see com.bialem.backend.domain.Event#cancelledAt
	 **/
	public static volatile SingularAttribute<Event, Instant> cancelledAt;
	
	/**
	 * @see com.bialem.backend.domain.Event#addressText
	 **/
	public static volatile SingularAttribute<Event, String> addressText;
	
	/**
	 * @see com.bialem.backend.domain.Event#community
	 **/
	public static volatile SingularAttribute<Event, Community> community;
	
	/**
	 * @see com.bialem.backend.domain.Event#createdBy
	 **/
	public static volatile SingularAttribute<Event, Profile> createdBy;
	
	/**
	 * @see com.bialem.backend.domain.Event#publishedToDiscovery
	 **/
	public static volatile SingularAttribute<Event, Boolean> publishedToDiscovery;
	
	/**
	 * @see com.bialem.backend.domain.Event#platformModerationStatus
	 **/
	public static volatile SingularAttribute<Event, PlatformModerationStatus> platformModerationStatus;
	
	/**
	 * @see com.bialem.backend.domain.Event#messages
	 **/
	public static volatile SetAttribute<Event, EventMessage> messages;
	
	/**
	 * @see com.bialem.backend.domain.Event#rejectionReason
	 **/
	public static volatile SingularAttribute<Event, String> rejectionReason;
	
	/**
	 * @see com.bialem.backend.domain.Event#category
	 **/
	public static volatile SingularAttribute<Event, Community> category;
	
	/**
	 * @see com.bialem.backend.domain.Event#status
	 **/
	public static volatile SingularAttribute<Event, EventStatus> status;

}

