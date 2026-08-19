package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(UserHonorBadge.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class UserHonorBadge_ {

	public static final String AWARDED_AT = "awardedAt";
	public static final String REASON = "reason";
	public static final String BADGE = "badge";
	public static final String ID = "id";
	public static final String AWARDED_BY = "awardedBy";
	public static final String USER = "user";

	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge#awardedAt
	 **/
	public static volatile SingularAttribute<UserHonorBadge, Instant> awardedAt;
	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge#reason
	 **/
	public static volatile SingularAttribute<UserHonorBadge, String> reason;
	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge#badge
	 **/
	public static volatile SingularAttribute<UserHonorBadge, HonorBadge> badge;
	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge#id
	 **/
	public static volatile SingularAttribute<UserHonorBadge, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge#awardedBy
	 **/
	public static volatile SingularAttribute<UserHonorBadge, Profile> awardedBy;
	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge
	 **/
	public static volatile EntityType<UserHonorBadge> class_;
	
	/**
	 * @see com.bialem.backend.domain.UserHonorBadge#user
	 **/
	public static volatile SingularAttribute<UserHonorBadge, Profile> user;

}

