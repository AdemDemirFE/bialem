package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(FollowRequest.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class FollowRequest_ {

	public static final String REQUESTER = "requester";
	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String TARGET_USER = "targetUser";

	
	/**
	 * @see com.bialem.backend.domain.FollowRequest#requester
	 **/
	public static volatile SingularAttribute<FollowRequest, Profile> requester;
	
	/**
	 * @see com.bialem.backend.domain.FollowRequest#createdAt
	 **/
	public static volatile SingularAttribute<FollowRequest, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.FollowRequest#id
	 **/
	public static volatile SingularAttribute<FollowRequest, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.FollowRequest
	 **/
	public static volatile EntityType<FollowRequest> class_;
	
	/**
	 * @see com.bialem.backend.domain.FollowRequest#targetUser
	 **/
	public static volatile SingularAttribute<FollowRequest, Profile> targetUser;

}

