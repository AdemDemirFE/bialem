package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.CommentTargetType;
import com.bialem.backend.domain.enumeration.ModerationStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Comment.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Comment_ {

	public static final String CREATED_AT = "createdAt";
	public static final String TARGET_ID = "targetId";
	public static final String AUTHOR = "author";
	public static final String MODERATION_STATUS = "moderationStatus";
	public static final String TARGET_TYPE = "targetType";
	public static final String ID = "id";
	public static final String BODY = "body";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.Comment#createdAt
	 **/
	public static volatile SingularAttribute<Comment, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Comment#targetId
	 **/
	public static volatile SingularAttribute<Comment, String> targetId;
	
	/**
	 * @see com.bialem.backend.domain.Comment#author
	 **/
	public static volatile SingularAttribute<Comment, Profile> author;
	
	/**
	 * @see com.bialem.backend.domain.Comment#moderationStatus
	 **/
	public static volatile SingularAttribute<Comment, ModerationStatus> moderationStatus;
	
	/**
	 * @see com.bialem.backend.domain.Comment#targetType
	 **/
	public static volatile SingularAttribute<Comment, CommentTargetType> targetType;
	
	/**
	 * @see com.bialem.backend.domain.Comment#id
	 **/
	public static volatile SingularAttribute<Comment, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Comment#body
	 **/
	public static volatile SingularAttribute<Comment, String> body;
	
	/**
	 * @see com.bialem.backend.domain.Comment
	 **/
	public static volatile EntityType<Comment> class_;
	
	/**
	 * @see com.bialem.backend.domain.Comment#updatedAt
	 **/
	public static volatile SingularAttribute<Comment, Instant> updatedAt;

}

