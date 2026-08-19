package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(EventRating.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class EventRating_ {

	public static final String CREATED_AT = "createdAt";
	public static final String RATING = "rating";
	public static final String ID = "id";
	public static final String EVENT = "event";
	public static final String USER = "user";
	public static final String REVIEW_TEXT = "reviewText";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.EventRating#createdAt
	 **/
	public static volatile SingularAttribute<EventRating, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.EventRating#rating
	 **/
	public static volatile SingularAttribute<EventRating, Integer> rating;
	
	/**
	 * @see com.bialem.backend.domain.EventRating#id
	 **/
	public static volatile SingularAttribute<EventRating, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.EventRating#event
	 **/
	public static volatile SingularAttribute<EventRating, Event> event;
	
	/**
	 * @see com.bialem.backend.domain.EventRating
	 **/
	public static volatile EntityType<EventRating> class_;
	
	/**
	 * @see com.bialem.backend.domain.EventRating#user
	 **/
	public static volatile SingularAttribute<EventRating, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.EventRating#reviewText
	 **/
	public static volatile SingularAttribute<EventRating, String> reviewText;
	
	/**
	 * @see com.bialem.backend.domain.EventRating#updatedAt
	 **/
	public static volatile SingularAttribute<EventRating, Instant> updatedAt;

}

