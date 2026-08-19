package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.MediaType;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(PostMedia.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PostMedia_ {

	public static final String CREATED_AT = "createdAt";
	public static final String POST = "post";
	public static final String SORT_ORDER = "sortOrder";
	public static final String MEDIA_TYPE = "mediaType";
	public static final String STORAGE_PATH = "storagePath";
	public static final String ID = "id";

	
	/**
	 * @see com.bialem.backend.domain.PostMedia#createdAt
	 **/
	public static volatile SingularAttribute<PostMedia, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PostMedia#post
	 **/
	public static volatile SingularAttribute<PostMedia, Post> post;
	
	/**
	 * @see com.bialem.backend.domain.PostMedia#sortOrder
	 **/
	public static volatile SingularAttribute<PostMedia, Integer> sortOrder;
	
	/**
	 * @see com.bialem.backend.domain.PostMedia#mediaType
	 **/
	public static volatile SingularAttribute<PostMedia, MediaType> mediaType;
	
	/**
	 * @see com.bialem.backend.domain.PostMedia#storagePath
	 **/
	public static volatile SingularAttribute<PostMedia, String> storagePath;
	
	/**
	 * @see com.bialem.backend.domain.PostMedia#id
	 **/
	public static volatile SingularAttribute<PostMedia, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PostMedia
	 **/
	public static volatile EntityType<PostMedia> class_;

}

