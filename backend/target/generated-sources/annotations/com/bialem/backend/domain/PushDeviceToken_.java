package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PushPlatform;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(PushDeviceToken.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PushDeviceToken_ {

	public static final String LAST_SEEN_AT = "lastSeenAt";
	public static final String DEVICE_UUID = "deviceUuid";
	public static final String APP_VERSION = "appVersion";
	public static final String LAST_FAILURE_AT = "lastFailureAt";
	public static final String ACTIVE = "active";
	public static final String NOTIFICATIONS_ENABLED = "notificationsEnabled";
	public static final String FIREBASE_INSTALLATION_ID = "firebaseInstallationId";
	public static final String PLATFORM = "platform";
	public static final String TOKEN = "token";
	public static final String CREATED_AT = "createdAt";
	public static final String LAST_SUCCESS_AT = "lastSuccessAt";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#lastSeenAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> lastSeenAt;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#deviceUuid
	 **/
	public static volatile SingularAttribute<PushDeviceToken, String> deviceUuid;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#appVersion
	 **/
	public static volatile SingularAttribute<PushDeviceToken, String> appVersion;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#lastFailureAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> lastFailureAt;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#active
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Boolean> active;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#notificationsEnabled
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Boolean> notificationsEnabled;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#firebaseInstallationId
	 **/
	public static volatile SingularAttribute<PushDeviceToken, String> firebaseInstallationId;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#platform
	 **/
	public static volatile SingularAttribute<PushDeviceToken, PushPlatform> platform;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#token
	 **/
	public static volatile SingularAttribute<PushDeviceToken, String> token;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#createdAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#lastSuccessAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> lastSuccessAt;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#id
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken
	 **/
	public static volatile EntityType<PushDeviceToken> class_;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#user
	 **/
	public static volatile SingularAttribute<PushDeviceToken, User> user;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#updatedAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> updatedAt;

}

