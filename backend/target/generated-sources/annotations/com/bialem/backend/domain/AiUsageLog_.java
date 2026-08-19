package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(AiUsageLog.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class AiUsageLog_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String USER = "user";

	
	/**
	 * @see com.bialem.backend.domain.AiUsageLog#createdAt
	 **/
	public static volatile SingularAttribute<AiUsageLog, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.AiUsageLog#id
	 **/
	public static volatile SingularAttribute<AiUsageLog, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.AiUsageLog
	 **/
	public static volatile EntityType<AiUsageLog> class_;
	
	/**
	 * @see com.bialem.backend.domain.AiUsageLog#user
	 **/
	public static volatile SingularAttribute<AiUsageLog, Profile> user;

}

