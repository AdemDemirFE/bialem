package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(UserNotificationPreference.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class UserNotificationPreference_ {

	public static final String CREATED_AT = "createdAt";
	public static final String IN_APP_ENABLED = "inAppEnabled";
	public static final String EMAIL_ENABLED = "emailEnabled";
	public static final String PUSH_ENABLED = "pushEnabled";
	public static final String ID = "id";
	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String MUTED_UNTIL = "mutedUntil";
	public static final String USER = "user";
	public static final String MANDATORY = "mandatory";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#createdAt
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#inAppEnabled
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Boolean> inAppEnabled;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#emailEnabled
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Boolean> emailEnabled;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#pushEnabled
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Boolean> pushEnabled;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#id
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#notificationType
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, String> notificationType;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#mutedUntil
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Instant> mutedUntil;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference
	 **/
	public static volatile EntityType<UserNotificationPreference> class_;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#user
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, User> user;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#mandatory
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Boolean> mandatory;
	
	/**
	 * @see com.bialem.backend.domain.UserNotificationPreference#updatedAt
	 **/
	public static volatile SingularAttribute<UserNotificationPreference, Instant> updatedAt;

}

