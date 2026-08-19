package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.EventParticipantStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(EventParticipant.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class EventParticipant_ {

	public static final String NOTE = "note";
	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String EVENT = "event";
	public static final String USER = "user";
	public static final String STATUS = "status";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#note
	 **/
	public static volatile SingularAttribute<EventParticipant, String> note;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#createdAt
	 **/
	public static volatile SingularAttribute<EventParticipant, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#id
	 **/
	public static volatile SingularAttribute<EventParticipant, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#event
	 **/
	public static volatile SingularAttribute<EventParticipant, Event> event;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant
	 **/
	public static volatile EntityType<EventParticipant> class_;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#user
	 **/
	public static volatile SingularAttribute<EventParticipant, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#status
	 **/
	public static volatile SingularAttribute<EventParticipant, EventParticipantStatus> status;
	
	/**
	 * @see com.bialem.backend.domain.EventParticipant#updatedAt
	 **/
	public static volatile SingularAttribute<EventParticipant, Instant> updatedAt;

}

