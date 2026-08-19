package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.CommunityMemberRole;
import com.bialem.backend.domain.enumeration.CommunityMemberStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(CommunityMember.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CommunityMember_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ROLE = "role";
	public static final String ID = "id";
	public static final String COMMUNITY = "community";
	public static final String USER = "user";
	public static final String STATUS = "status";

	
	/**
	 * @see com.bialem.backend.domain.CommunityMember#createdAt
	 **/
	public static volatile SingularAttribute<CommunityMember, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.CommunityMember#role
	 **/
	public static volatile SingularAttribute<CommunityMember, CommunityMemberRole> role;
	
	/**
	 * @see com.bialem.backend.domain.CommunityMember#id
	 **/
	public static volatile SingularAttribute<CommunityMember, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.CommunityMember#community
	 **/
	public static volatile SingularAttribute<CommunityMember, Community> community;
	
	/**
	 * @see com.bialem.backend.domain.CommunityMember
	 **/
	public static volatile EntityType<CommunityMember> class_;
	
	/**
	 * @see com.bialem.backend.domain.CommunityMember#user
	 **/
	public static volatile SingularAttribute<CommunityMember, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.CommunityMember#status
	 **/
	public static volatile SingularAttribute<CommunityMember, CommunityMemberStatus> status;

}

