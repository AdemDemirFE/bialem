package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.NotificationEventType;
import com.bialem.backend.domain.enumeration.NotificationPriority;
import com.bialem.backend.domain.enumeration.NotificationScheduleType;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(NotificationTemplate.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class NotificationTemplate_ {

	public static final String ROUTE_TEMPLATE = "routeTemplate";
	public static final String CODE = "code";
	public static final String UPDATED_BY = "updatedBy";
	public static final String IN_APP_ENABLED = "inAppEnabled";
	public static final String TIMEZONE = "timezone";
	public static final String EVENT_TYPE = "eventType";
	public static final String PRIORITY = "priority";
	public static final String ENABLED = "enabled";
	public static final String TARGET_STRATEGY = "targetStrategy";
	public static final String CREATED_AT = "createdAt";
	public static final String BODY_TEMPLATE = "bodyTemplate";
	public static final String SCHEDULE_TYPE = "scheduleType";
	public static final String DELAY_MINUTES = "delayMinutes";
	public static final String PREFERRED_SEND_TIME = "preferredSendTime";
	public static final String CREATED_BY = "createdBy";
	public static final String NAME = "name";
	public static final String PUSH_ENABLED = "pushEnabled";
	public static final String ID = "id";
	public static final String TITLE_TEMPLATE = "titleTemplate";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#routeTemplate
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> routeTemplate;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#code
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> code;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#updatedBy
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> updatedBy;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#inAppEnabled
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Boolean> inAppEnabled;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#timezone
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> timezone;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#eventType
	 **/
	public static volatile SingularAttribute<NotificationTemplate, NotificationEventType> eventType;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#priority
	 **/
	public static volatile SingularAttribute<NotificationTemplate, NotificationPriority> priority;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#enabled
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Boolean> enabled;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#targetStrategy
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> targetStrategy;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#createdAt
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#bodyTemplate
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> bodyTemplate;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#scheduleType
	 **/
	public static volatile SingularAttribute<NotificationTemplate, NotificationScheduleType> scheduleType;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#delayMinutes
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Integer> delayMinutes;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#preferredSendTime
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> preferredSendTime;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#createdBy
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> createdBy;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#name
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> name;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#pushEnabled
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Boolean> pushEnabled;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#id
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate
	 **/
	public static volatile EntityType<NotificationTemplate> class_;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#titleTemplate
	 **/
	public static volatile SingularAttribute<NotificationTemplate, String> titleTemplate;
	
	/**
	 * @see com.bialem.backend.domain.NotificationTemplate#updatedAt
	 **/
	public static volatile SingularAttribute<NotificationTemplate, Instant> updatedAt;

}

