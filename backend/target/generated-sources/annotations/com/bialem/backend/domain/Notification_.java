package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Notification.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Notification_ {

	public static final String IS_READ = "isRead";
	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	public static final String BODY = "body";
	public static final String READ_AT = "readAt";
	public static final String REFERENCE_ID = "referenceId";
	public static final String CREATED_AT = "createdAt";
	public static final String ROUTE = "route";
	public static final String PAYLOAD = "payload";
	public static final String ID = "id";
	public static final String USER = "user";

	
	/**
	 * @see com.bialem.backend.domain.Notification#isRead
	 **/
	public static volatile SingularAttribute<Notification, Boolean> isRead;
	
	/**
	 * @see com.bialem.backend.domain.Notification#notificationType
	 **/
	public static volatile SingularAttribute<Notification, String> notificationType;
	
	/**
	 * @see com.bialem.backend.domain.Notification#type
	 **/
	public static volatile SingularAttribute<Notification, String> type;
	
	/**
	 * @see com.bialem.backend.domain.Notification#title
	 **/
	public static volatile SingularAttribute<Notification, String> title;
	
	/**
	 * @see com.bialem.backend.domain.Notification#body
	 **/
	public static volatile SingularAttribute<Notification, String> body;
	
	/**
	 * @see com.bialem.backend.domain.Notification#readAt
	 **/
	public static volatile SingularAttribute<Notification, Instant> readAt;
	
	/**
	 * @see com.bialem.backend.domain.Notification#referenceId
	 **/
	public static volatile SingularAttribute<Notification, String> referenceId;
	
	/**
	 * @see com.bialem.backend.domain.Notification#createdAt
	 **/
	public static volatile SingularAttribute<Notification, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Notification#route
	 **/
	public static volatile SingularAttribute<Notification, String> route;
	
	/**
	 * @see com.bialem.backend.domain.Notification#payload
	 **/
	public static volatile SingularAttribute<Notification, String> payload;
	
	/**
	 * @see com.bialem.backend.domain.Notification#id
	 **/
	public static volatile SingularAttribute<Notification, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Notification
	 **/
	public static volatile EntityType<Notification> class_;
	
	/**
	 * @see com.bialem.backend.domain.Notification#user
	 **/
	public static volatile SingularAttribute<Notification, Profile> user;

}

