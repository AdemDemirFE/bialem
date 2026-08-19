package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Block.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Block_ {

	public static final String CREATED_AT = "createdAt";
	public static final String BLOCKED_USER = "blockedUser";
	public static final String BLOCKER = "blocker";
	public static final String ID = "id";

	
	/**
	 * @see com.bialem.backend.domain.Block#createdAt
	 **/
	public static volatile SingularAttribute<Block, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Block#blockedUser
	 **/
	public static volatile SingularAttribute<Block, Profile> blockedUser;
	
	/**
	 * @see com.bialem.backend.domain.Block#blocker
	 **/
	public static volatile SingularAttribute<Block, Profile> blocker;
	
	/**
	 * @see com.bialem.backend.domain.Block#id
	 **/
	public static volatile SingularAttribute<Block, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Block
	 **/
	public static volatile EntityType<Block> class_;

}

