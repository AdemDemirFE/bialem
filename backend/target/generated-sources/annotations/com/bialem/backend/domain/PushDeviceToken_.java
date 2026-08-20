package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PushPlatform;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(PushDeviceToken.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PushDeviceToken_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String PLATFORM = "platform";
	public static final String TOKEN = "token";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#createdAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#id
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken
	 **/
	public static volatile EntityType<PushDeviceToken> class_;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#user
	 **/
	public static volatile SingularAttribute<PushDeviceToken, User> user;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#platform
	 **/
	public static volatile SingularAttribute<PushDeviceToken, PushPlatform> platform;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#token
	 **/
	public static volatile SingularAttribute<PushDeviceToken, String> token;
	
	/**
	 * @see com.bialem.backend.domain.PushDeviceToken#updatedAt
	 **/
	public static volatile SingularAttribute<PushDeviceToken, Instant> updatedAt;

}

