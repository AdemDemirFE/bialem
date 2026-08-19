package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.ModerationStatus;
import com.bialem.backend.domain.enumeration.PostVisibility;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Post.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Post_ {

	public static final String CREATED_AT = "createdAt";
	public static final String VISIBILITY = "visibility";
	public static final String AUTHOR = "author";
	public static final String MODERATION_STATUS = "moderationStatus";
	public static final String ID = "id";
	public static final String MEDIA = "media";
	public static final String BODY = "body";
	public static final String COMMUNITY = "community";
	public static final String EVENT = "event";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.Post#createdAt
	 **/
	public static volatile SingularAttribute<Post, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Post#visibility
	 **/
	public static volatile SingularAttribute<Post, PostVisibility> visibility;
	
	/**
	 * @see com.bialem.backend.domain.Post#author
	 **/
	public static volatile SingularAttribute<Post, Profile> author;
	
	/**
	 * @see com.bialem.backend.domain.Post#moderationStatus
	 **/
	public static volatile SingularAttribute<Post, ModerationStatus> moderationStatus;
	
	/**
	 * @see com.bialem.backend.domain.Post#id
	 **/
	public static volatile SingularAttribute<Post, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Post#media
	 **/
	public static volatile SetAttribute<Post, PostMedia> media;
	
	/**
	 * @see com.bialem.backend.domain.Post#body
	 **/
	public static volatile SingularAttribute<Post, String> body;
	
	/**
	 * @see com.bialem.backend.domain.Post#community
	 **/
	public static volatile SingularAttribute<Post, Community> community;
	
	/**
	 * @see com.bialem.backend.domain.Post#event
	 **/
	public static volatile SingularAttribute<Post, Event> event;
	
	/**
	 * @see com.bialem.backend.domain.Post
	 **/
	public static volatile EntityType<Post> class_;
	
	/**
	 * @see com.bialem.backend.domain.Post#updatedAt
	 **/
	public static volatile SingularAttribute<Post, Instant> updatedAt;

}

