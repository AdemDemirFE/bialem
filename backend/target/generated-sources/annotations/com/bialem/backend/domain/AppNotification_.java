package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(AppNotification.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class AppNotification_ {

	public static final String TEMPLATE = "template";
	public static final String EVENT_ID = "eventId";
	public static final String PUSH_STATUS = "pushStatus";
	public static final String IDEMPOTENCY_KEY = "idempotencyKey";
	public static final String IS_READ = "isRead";
	public static final String NOTIFICATION_TYPE = "notificationType";
	public static final String TITLE = "title";
	public static final String BODY = "body";
	public static final String READ_AT = "readAt";
	public static final String REFERENCE_ID = "referenceId";
	public static final String CREATED_AT = "createdAt";
	public static final String ROUTE = "route";
	public static final String PAYLOAD = "payload";
	public static final String PUSH_SENT_AT = "pushSentAt";
	public static final String CORRELATION_ID = "correlationId";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String SCHEDULED_AT = "scheduledAt";

	
	/**
	 * @see com.bialem.backend.domain.AppNotification#template
	 **/
	public static volatile SingularAttribute<AppNotification, NotificationTemplate> template;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#eventId
	 **/
	public static volatile SingularAttribute<AppNotification, String> eventId;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#pushStatus
	 **/
	public static volatile SingularAttribute<AppNotification, String> pushStatus;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#idempotencyKey
	 **/
	public static volatile SingularAttribute<AppNotification, String> idempotencyKey;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#isRead
	 **/
	public static volatile SingularAttribute<AppNotification, Boolean> isRead;
	
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
	 * @see com.bialem.backend.domain.AppNotification#referenceId
	 **/
	public static volatile SingularAttribute<AppNotification, String> referenceId;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#createdAt
	 **/
	public static volatile SingularAttribute<AppNotification, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#route
	 **/
	public static volatile SingularAttribute<AppNotification, String> route;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#payload
	 **/
	public static volatile SingularAttribute<AppNotification, String> payload;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#pushSentAt
	 **/
	public static volatile SingularAttribute<AppNotification, Instant> pushSentAt;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#correlationId
	 **/
	public static volatile SingularAttribute<AppNotification, String> correlationId;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#id
	 **/
	public static volatile SingularAttribute<AppNotification, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification
	 **/
	public static volatile EntityType<AppNotification> class_;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#user
	 **/
	public static volatile SingularAttribute<AppNotification, User> user;
	
	/**
	 * @see com.bialem.backend.domain.AppNotification#scheduledAt
	 **/
	public static volatile SingularAttribute<AppNotification, Instant> scheduledAt;

}

