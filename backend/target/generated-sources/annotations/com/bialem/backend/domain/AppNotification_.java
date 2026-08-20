package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(AppNotification.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class AppNotification_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ROUTE = "route";
	public static final String IS_READ = "isRead";
	public static final String ID = "id";
	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String TITLE = "title";
	public static final String BODY = "body";
	public static final String READ_AT = "readAt";
	public static final String USER = "user";
	public static final String REFERENCE_ID = "referenceId";

	
	/**
	 * @see com.bialem.backend.domain.AppNotification#createdAt
	 **/
	public static volatile SingularAttribute<AppNotification, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#route
	 **/
	public static volatile SingularAttribute<AppNotification, String> route;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#isRead
	 **/
	public static volatile SingularAttribute<AppNotification, Boolean> isRead;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#id
	 **/
	public static volatile SingularAttribute<AppNotification, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#notificationType
	 **/
	public static volatile SingularAttribute<AppNotification, String> notificationType;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#title
	 **/
	public static volatile SingularAttribute<AppNotification, String> title;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#body
	 **/
	public static volatile SingularAttribute<AppNotification, String> body;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#readAt
	 **/
	public static volatile SingularAttribute<AppNotification, Instant> readAt;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification
	 **/
	public static volatile EntityType<AppNotification> class_;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#user
	 **/
	public static volatile SingularAttribute<AppNotification, User> user;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#referenceId
	 **/
	public static volatile SingularAttribute<AppNotification, String> referenceId;

}

