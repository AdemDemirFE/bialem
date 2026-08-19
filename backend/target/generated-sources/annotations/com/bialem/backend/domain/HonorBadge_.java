package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.HonorBadgeType;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(HonorBadge.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class HonorBadge_ {

	public static final String CREATED_AT = "createdAt";
	public static final String MINIMUM_CHECK_INS = "minimumCheckIns";
	public static final String CODE = "code";
	public static final String NAME_TEMPLATE = "nameTemplate";
	public static final String BADGE_TYPE = "badgeType";
	public static final String DESCRIPTION = "description";
	public static final String ID = "id";
	public static final String IS_ACTIVE = "isActive";
	public static final String COMMUNITY = "community";

	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#createdAt
	 **/
	public static volatile SingularAttribute<HonorBadge, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#minimumCheckIns
	 **/
	public static volatile SingularAttribute<HonorBadge, Integer> minimumCheckIns;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#code
	 **/
	public static volatile SingularAttribute<HonorBadge, String> code;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#nameTemplate
	 **/
	public static volatile SingularAttribute<HonorBadge, String> nameTemplate;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#badgeType
	 **/
	public static volatile SingularAttribute<HonorBadge, HonorBadgeType> badgeType;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#description
	 **/
	public static volatile SingularAttribute<HonorBadge, String> description;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#id
	 **/
	public static volatile SingularAttribute<HonorBadge, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#isActive
	 **/
	public static volatile SingularAttribute<HonorBadge, Boolean> isActive;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge#community
	 **/
	public static volatile SingularAttribute<HonorBadge, Community> community;
	
	/**
	 * @see com.bialem.backend.domain.HonorBadge
	 **/
	public static volatile EntityType<HonorBadge> class_;

}

