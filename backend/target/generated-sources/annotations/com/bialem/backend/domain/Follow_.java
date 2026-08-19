package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Follow.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Follow_ {

	public static final String CREATED_AT = "createdAt";
	public static final String FOLLOWER = "follower";
	public static final String ID = "id";
	public static final String FOLLOWED = "followed";

	
	/**
	 * @see com.bialem.backend.domain.Follow#createdAt
	 **/
	public static volatile SingularAttribute<Follow, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Follow#follower
	 **/
	public static volatile SingularAttribute<Follow, Profile> follower;
	
	/**
	 * @see com.bialem.backend.domain.Follow#id
	 **/
	public static volatile SingularAttribute<Follow, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Follow
	 **/
	public static volatile EntityType<Follow> class_;
	
	/**
	 * @see com.bialem.backend.domain.Follow#followed
	 **/
	public static volatile SingularAttribute<Follow, Profile> followed;

}

