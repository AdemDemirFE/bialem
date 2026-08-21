package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.NotificationDeliveryStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(NotificationDeliveryLog.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class NotificationDeliveryLog_ {

	public static final String ATTEMPT_NUMBER = "attemptNumber";
	public static final String NOTIFICATION = "notification";
	public static final String CREATED_AT = "createdAt";
	public static final String PROVIDER_MESSAGE_ID = "providerMessageId";
	public static final String PROVIDER = "provider";
	public static final String PUSH_DEVICE = "pushDevice";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String ERROR_CODE = "errorCode";
	public static final String ID = "id";
	public static final String SENT_AT = "sentAt";
	public static final String STATUS = "status";

	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#attemptNumber
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, Integer> attemptNumber;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#notification
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, AppNotification> notification;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#createdAt
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#providerMessageId
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, String> providerMessageId;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#provider
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, String> provider;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#pushDevice
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, PushDeviceToken> pushDevice;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#errorMessage
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, String> errorMessage;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#errorCode
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, String> errorCode;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#id
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#sentAt
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, Instant> sentAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog
	 **/
	public static volatile EntityType<NotificationDeliveryLog> class_;
	
	/**
	 * @see com.bialem.backend.domain.NotificationDeliveryLog#status
	 **/
	public static volatile SingularAttribute<NotificationDeliveryLog, NotificationDeliveryStatus> status;

}

