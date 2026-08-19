package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Role.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Role_ {

	public static final String CREATED_AT = "createdAt";
	public static final String CODE = "code";
	public static final String NAME = "name";
	public static final String ID = "id";

	
	/**
	 * @see com.bialem.backend.domain.Role#createdAt
	 **/
	public static volatile SingularAttribute<Role, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Role#code
	 **/
	public static volatile SingularAttribute<Role, String> code;
	
	/**
	 * @see com.bialem.backend.domain.Role#name
	 **/
	public static volatile SingularAttribute<Role, String> name;
	
	/**
	 * @see com.bialem.backend.domain.Role#id
	 **/
	public static volatile SingularAttribute<Role, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Role
	 **/
	public static volatile EntityType<Role> class_;

}

