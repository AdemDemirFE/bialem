package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.ModerationStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(EventMessage.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class EventMessage_ {

	public static final String CREATED_AT = "createdAt";
	public static final String AUTHOR = "author";
	public static final String MODERATION_STATUS = "moderationStatus";
	public static final String ID = "id";
	public static final String BODY = "body";
	public static final String EVENT = "event";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.EventMessage#createdAt
	 **/
	public static volatile SingularAttribute<EventMessage, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage#author
	 **/
	public static volatile SingularAttribute<EventMessage, Profile> author;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage#moderationStatus
	 **/
	public static volatile SingularAttribute<EventMessage, ModerationStatus> moderationStatus;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage#id
	 **/
	public static volatile SingularAttribute<EventMessage, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage#body
	 **/
	public static volatile SingularAttribute<EventMessage, String> body;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage#event
	 **/
	public static volatile SingularAttribute<EventMessage, Event> event;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage
	 **/
	public static volatile EntityType<EventMessage> class_;
	
	/**
	 * @see com.bialem.backend.domain.EventMessage#updatedAt
	 **/
	public static volatile SingularAttribute<EventMessage, Instant> updatedAt;

}

