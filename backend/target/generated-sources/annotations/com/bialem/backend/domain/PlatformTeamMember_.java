package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PlatformTeamRole;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(PlatformTeamMember.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PlatformTeamMember_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ASSIGNED_BY = "assignedBy";
	public static final String ROLE_CODE = "roleCode";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember#createdAt
	 **/
	public static volatile SingularAttribute<PlatformTeamMember, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember#assignedBy
	 **/
	public static volatile SingularAttribute<PlatformTeamMember, Profile> assignedBy;
	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember#roleCode
	 **/
	public static volatile SingularAttribute<PlatformTeamMember, PlatformTeamRole> roleCode;
	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember#id
	 **/
	public static volatile SingularAttribute<PlatformTeamMember, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember
	 **/
	public static volatile EntityType<PlatformTeamMember> class_;
	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember#user
	 **/
	public static volatile SingularAttribute<PlatformTeamMember, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.PlatformTeamMember#updatedAt
	 **/
	public static volatile SingularAttribute<PlatformTeamMember, Instant> updatedAt;

}

