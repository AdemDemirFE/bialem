package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.NotificationOutboxStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(NotificationOutbox.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class NotificationOutbox_ {

	public static final String LAST_ERROR = "lastError";
	public static final String IDEMPOTENCY_KEY = "idempotencyKey";
	public static final String ATTEMPT_COUNT = "attemptCount";
	public static final String NEXT_ATTEMPT_AT = "nextAttemptAt";
	public static final String SENT_AT = "sentAt";
	public static final String NOTIFICATION = "notification";
	public static final String MAX_ATTEMPTS = "maxAttempts";
	public static final String CREATED_AT = "createdAt";
	public static final String PROCESSED_AT = "processedAt";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String SCHEDULED_AT = "scheduledAt";
	public static final String STATUS = "status";

	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#lastError
	 **/
	public static volatile SingularAttribute<NotificationOutbox, String> lastError;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#idempotencyKey
	 **/
	public static volatile SingularAttribute<NotificationOutbox, String> idempotencyKey;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#attemptCount
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Integer> attemptCount;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#nextAttemptAt
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Instant> nextAttemptAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#sentAt
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Instant> sentAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#notification
	 **/
	public static volatile SingularAttribute<NotificationOutbox, AppNotification> notification;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#maxAttempts
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Integer> maxAttempts;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#createdAt
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#processedAt
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Instant> processedAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#id
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox
	 **/
	public static volatile EntityType<NotificationOutbox> class_;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#user
	 **/
	public static volatile SingularAttribute<NotificationOutbox, User> user;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#scheduledAt
	 **/
	public static volatile SingularAttribute<NotificationOutbox, Instant> scheduledAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationOutbox#status
	 **/
	public static volatile SingularAttribute<NotificationOutbox, NotificationOutboxStatus> status;

}

