package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(UserReview.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class UserReview_ {

	public static final String CREATED_AT = "createdAt";
	public static final String REVIEWED_USER = "reviewedUser";
	public static final String RATING = "rating";
	public static final String ID = "id";
	public static final String REVIEWER = "reviewer";
	public static final String EVENT = "event";
	public static final String REVIEW_TEXT = "reviewText";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.UserReview#createdAt
	 **/
	public static volatile SingularAttribute<UserReview, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#reviewedUser
	 **/
	public static volatile SingularAttribute<UserReview, Profile> reviewedUser;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#rating
	 **/
	public static volatile SingularAttribute<UserReview, Integer> rating;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#id
	 **/
	public static volatile SingularAttribute<UserReview, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#reviewer
	 **/
	public static volatile SingularAttribute<UserReview, Profile> reviewer;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#event
	 **/
	public static volatile SingularAttribute<UserReview, Event> event;
	
	/**
	 * @see com.bialem.backend.domain.UserReview
	 **/
	public static volatile EntityType<UserReview> class_;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#reviewText
	 **/
	public static volatile SingularAttribute<UserReview, String> reviewText;
	
	/**
	 * @see com.bialem.backend.domain.UserReview#updatedAt
	 **/
	public static volatile SingularAttribute<UserReview, Instant> updatedAt;

}

