package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.StoryContentType;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Story.class)
public abstract class Story_ {

	public static final String CREATED_AT = "createdAt";
	public static final String MEDIA_URL = "mediaUrl";
	public static final String AUTHOR = "author";
	public static final String IS_PUBLIC = "isPublic";
	public static final String SHARE_WITH_FOLLOWERS = "shareWithFollowers";
	public static final String ID = "id";
	public static final String COMMUNITY_TARGETS = "communityTargets";
	public static final String BODY = "body";
	public static final String CONTENT_TYPE = "contentType";
	public static final String EXPIRES_AT = "expiresAt";
	public static final String VIEWS = "views";

	
	/**
	 * @see com.bialem.backend.domain.Story#createdAt
	 **/
	public static volatile SingularAttribute<Story, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Story#mediaUrl
	 **/
	public static volatile SingularAttribute<Story, String> mediaUrl;
	
	/**
	 * @see com.bialem.backend.domain.Story#author
	 **/
	public static volatile SingularAttribute<Story, Profile> author;
	
	/**
	 * @see com.bialem.backend.domain.Story#isPublic
	 **/
	public static volatile SingularAttribute<Story, Boolean> isPublic;
	
	/**
	 * @see com.bialem.backend.domain.Story#shareWithFollowers
	 **/
	public static volatile SingularAttribute<Story, Boolean> shareWithFollowers;
	
	/**
	 * @see com.bialem.backend.domain.Story#id
	 **/
	public static volatile SingularAttribute<Story, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Story#communityTargets
	 **/
	public static volatile SetAttribute<Story, StoryCommunityTarget> communityTargets;
	
	/**
	 * @see com.bialem.backend.domain.Story#body
	 **/
	public static volatile SingularAttribute<Story, String> body;
	
	/**
	 * @see com.bialem.backend.domain.Story
	 **/
	public static volatile EntityType<Story> class_;
	
	/**
	 * @see com.bialem.backend.domain.Story#contentType
	 **/
	public static volatile SingularAttribute<Story, StoryContentType> contentType;
	
	/**
	 * @see com.bialem.backend.domain.Story#expiresAt
	 **/
	public static volatile SingularAttribute<Story, Instant> expiresAt;
	
	/**
	 * @see com.bialem.backend.domain.Story#views
	 **/
	public static volatile SetAttribute<Story, StoryView> views;

}

