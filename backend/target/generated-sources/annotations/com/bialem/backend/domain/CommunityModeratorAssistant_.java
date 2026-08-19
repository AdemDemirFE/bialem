package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(CommunityModeratorAssistant.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CommunityModeratorAssistant_ {

	public static final String CREATED_AT = "createdAt";
	public static final String CAN_MANAGE_PARTICIPANTS = "canManageParticipants";
	public static final String CAN_REVIEW_EVENTS = "canReviewEvents";
	public static final String ID = "id";
	public static final String COMMUNITY = "community";
	public static final String CAN_MANAGE_GROUPS = "canManageGroups";
	public static final String USER = "user";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#createdAt
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#canManageParticipants
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Boolean> canManageParticipants;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#canReviewEvents
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Boolean> canReviewEvents;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#id
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#community
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Community> community;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant
	 **/
	public static volatile EntityType<CommunityModeratorAssistant> class_;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#canManageGroups
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Boolean> canManageGroups;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#user
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.CommunityModeratorAssistant#updatedAt
	 **/
	public static volatile SingularAttribute<CommunityModeratorAssistant, Instant> updatedAt;

}

