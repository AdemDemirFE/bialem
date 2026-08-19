package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(PartnerVenueStaff.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PartnerVenueStaff_ {

	public static final String CREATED_AT = "createdAt";
	public static final String VENUE = "venue";
	public static final String ID = "id";
	public static final String IS_ACTIVE = "isActive";
	public static final String USER = "user";

	
	/**
	 * @see com.bialem.backend.domain.PartnerVenueStaff#createdAt
	 **/
	public static volatile SingularAttribute<PartnerVenueStaff, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenueStaff#venue
	 **/
	public static volatile SingularAttribute<PartnerVenueStaff, PartnerVenue> venue;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenueStaff#id
	 **/
	public static volatile SingularAttribute<PartnerVenueStaff, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenueStaff#isActive
	 **/
	public static volatile SingularAttribute<PartnerVenueStaff, Boolean> isActive;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenueStaff
	 **/
	public static volatile EntityType<PartnerVenueStaff> class_;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenueStaff#user
	 **/
	public static volatile SingularAttribute<PartnerVenueStaff, Profile> user;

}

