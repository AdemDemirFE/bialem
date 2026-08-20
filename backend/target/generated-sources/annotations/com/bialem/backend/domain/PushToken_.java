package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PushPlatform;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(PushToken.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PushToken_ {

	public static final String LAST_SEEN_AT = "lastSeenAt";
	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String IS_ACTIVE = "isActive";
	public static final String DEVICE_NAME = "deviceName";
	public static final String USER = "user";
	public static final String PLATFORM = "platform";
	public static final String DEVICE_TOKEN = "deviceToken";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.PushToken#lastSeenAt
	 **/
	public static volatile SingularAttribute<PushToken, Instant> lastSeenAt;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#createdAt
	 **/
	public static volatile SingularAttribute<PushToken, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#id
	 **/
	public static volatile SingularAttribute<PushToken, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#isActive
	 **/
	public static volatile SingularAttribute<PushToken, Boolean> isActive;
	
	/**
	 * @see com.bialem.backend.domain.PushToken
	 **/
	public static volatile EntityType<PushToken> class_;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#deviceName
	 **/
	public static volatile SingularAttribute<PushToken, String> deviceName;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#user
	 **/
	public static volatile SingularAttribute<PushToken, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#platform
	 **/
	public static volatile SingularAttribute<PushToken, PushPlatform> platform;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#deviceToken
	 **/
	public static volatile SingularAttribute<PushToken, String> deviceToken;
	
	/**
	 * @see com.bialem.backend.domain.PushToken#updatedAt
	 **/
	public static volatile SingularAttribute<PushToken, Instant> updatedAt;

}

