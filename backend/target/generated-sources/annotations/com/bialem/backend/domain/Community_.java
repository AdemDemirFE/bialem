package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.CommunityType;
import com.bialem.backend.domain.enumeration.CommunityVisibility;
import com.bialem.backend.domain.enumeration.PartnerTrustLevel;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Community.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Community_ {

	public static final String LEAD_MODERATOR = "leadModerator";
	public static final String PARENT = "parent";
	public static final String ASSISTANTS = "assistants";
	public static final String CATEGORIZED_GROUPS = "categorizedGroups";
	public static final String VISIBILITY = "visibility";
	public static final String COVER_IMAGE_URL = "coverImageUrl";
	public static final String DESCRIPTION = "description";
	public static final String PARTNER_TRUST_LEVEL = "partnerTrustLevel";
	public static final String POSTS = "posts";
	public static final String CREATED_AT = "createdAt";
	public static final String COMMUNITY_TYPE = "communityType";
	public static final String CREATED_BY = "createdBy";
	public static final String CHILDREN = "children";
	public static final String MEMBERS = "members";
	public static final String NAME = "name";
	public static final String IS_DISCOVERABLE = "isDiscoverable";
	public static final String ID = "id";
	public static final String STORY_TARGETS = "storyTargets";
	public static final String SLUG = "slug";
	public static final String IS_VERIFIED_PARTNER = "isVerifiedPartner";
	public static final String EVENTS = "events";
	public static final String UPDATED_AT = "updatedAt";
	public static final String CATEGORY_HUB = "categoryHub";

	
	/**
	 * @see com.bialem.backend.domain.Community#leadModerator
	 **/
	public static volatile SingularAttribute<Community, Profile> leadModerator;
	
	/**
	 * @see com.bialem.backend.domain.Community#parent
	 **/
	public static volatile SingularAttribute<Community, Community> parent;
	
	/**
	 * @see com.bialem.backend.domain.Community#assistants
	 **/
	public static volatile SetAttribute<Community, CommunityModeratorAssistant> assistants;
	
	/**
	 * @see com.bialem.backend.domain.Community#categorizedGroups
	 **/
	public static volatile SetAttribute<Community, Community> categorizedGroups;
	
	/**
	 * @see com.bialem.backend.domain.Community#visibility
	 **/
	public static volatile SingularAttribute<Community, CommunityVisibility> visibility;
	
	/**
	 * @see com.bialem.backend.domain.Community#coverImageUrl
	 **/
	public static volatile SingularAttribute<Community, String> coverImageUrl;
	
	/**
	 * @see com.bialem.backend.domain.Community#description
	 **/
	public static volatile SingularAttribute<Community, String> description;
	
	/**
	 * @see com.bialem.backend.domain.Community#partnerTrustLevel
	 **/
	public static volatile SingularAttribute<Community, PartnerTrustLevel> partnerTrustLevel;
	
	/**
	 * @see com.bialem.backend.domain.Community#posts
	 **/
	public static volatile SetAttribute<Community, Post> posts;
	
	/**
	 * @see com.bialem.backend.domain.Community#createdAt
	 **/
	public static volatile SingularAttribute<Community, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Community#communityType
	 **/
	public static volatile SingularAttribute<Community, CommunityType> communityType;
	
	/**
	 * @see com.bialem.backend.domain.Community#createdBy
	 **/
	public static volatile SingularAttribute<Community, Profile> createdBy;
	
	/**
	 * @see com.bialem.backend.domain.Community#children
	 **/
	public static volatile SetAttribute<Community, Community> children;
	
	/**
	 * @see com.bialem.backend.domain.Community#members
	 **/
	public static volatile SetAttribute<Community, CommunityMember> members;
	
	/**
	 * @see com.bialem.backend.domain.Community#name
	 **/
	public static volatile SingularAttribute<Community, String> name;
	
	/**
	 * @see com.bialem.backend.domain.Community#isDiscoverable
	 **/
	public static volatile SingularAttribute<Community, Boolean> isDiscoverable;
	
	/**
	 * @see com.bialem.backend.domain.Community#id
	 **/
	public static volatile SingularAttribute<Community, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Community#storyTargets
	 **/
	public static volatile SetAttribute<Community, StoryCommunityTarget> storyTargets;
	
	/**
	 * @see com.bialem.backend.domain.Community
	 **/
	public static volatile EntityType<Community> class_;
	
	/**
	 * @see com.bialem.backend.domain.Community#slug
	 **/
	public static volatile SingularAttribute<Community, String> slug;
	
	/**
	 * @see com.bialem.backend.domain.Community#isVerifiedPartner
	 **/
	public static volatile SingularAttribute<Community, Boolean> isVerifiedPartner;
	
	/**
	 * @see com.bialem.backend.domain.Community#events
	 **/
	public static volatile SetAttribute<Community, Event> events;
	
	/**
	 * @see com.bialem.backend.domain.Community#updatedAt
	 **/
	public static volatile SingularAttribute<Community, Instant> updatedAt;
	
	/**
	 * @see com.bialem.backend.domain.Community#categoryHub
	 **/
	public static volatile SingularAttribute<Community, Community> categoryHub;

}

