package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(UserRole.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class UserRole_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ROLE = "role";
	public static final String ID = "id";
	public static final String USER = "user";

	
	/**
	 * @see com.bialem.backend.domain.UserRole#createdAt
	 **/
	public static volatile SingularAttribute<UserRole, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.UserRole#role
	 **/
	public static volatile SingularAttribute<UserRole, Role> role;
	
	/**
	 * @see com.bialem.backend.domain.UserRole#id
	 **/
	public static volatile SingularAttribute<UserRole, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.UserRole
	 **/
	public static volatile EntityType<UserRole> class_;
	
	/**
	 * @see com.bialem.backend.domain.UserRole#user
	 **/
	public static volatile SingularAttribute<UserRole, Profile> user;

}

