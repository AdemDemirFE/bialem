package com.bialem.backend.domain;

import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(StoryCommunityTarget.class)
public abstract class StoryCommunityTarget_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String COMMUNITY = "community";
	public static final String STORY = "story";

	
	/**
	 * @see com.bialem.backend.domain.StoryCommunityTarget#createdAt
	 **/
	public static volatile SingularAttribute<StoryCommunityTarget, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.StoryCommunityTarget#id
	 **/
	public static volatile SingularAttribute<StoryCommunityTarget, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.StoryCommunityTarget#community
	 **/
	public static volatile SingularAttribute<StoryCommunityTarget, Community> community;
	
	/**
	 * @see com.bialem.backend.domain.StoryCommunityTarget
	 **/
	public static volatile EntityType<StoryCommunityTarget> class_;
	
	/**
	 * @see com.bialem.backend.domain.StoryCommunityTarget#story
	 **/
	public static volatile SingularAttribute<StoryCommunityTarget, Story> story;

}

