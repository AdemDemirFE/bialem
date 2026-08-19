package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(CityEventInterest.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CityEventInterest_ {

	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String LOOKING_FOR_COMPANY = "lookingForCompany";
	public static final String CITY_EVENT = "cityEvent";
	public static final String USER = "user";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest#createdAt
	 **/
	public static volatile SingularAttribute<CityEventInterest, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest#id
	 **/
	public static volatile SingularAttribute<CityEventInterest, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest#lookingForCompany
	 **/
	public static volatile SingularAttribute<CityEventInterest, Boolean> lookingForCompany;
	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest#cityEvent
	 **/
	public static volatile SingularAttribute<CityEventInterest, CityEvent> cityEvent;
	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest
	 **/
	public static volatile EntityType<CityEventInterest> class_;
	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest#user
	 **/
	public static volatile SingularAttribute<CityEventInterest, Profile> user;
	
	/**
	 * @see com.bialem.backend.domain.CityEventInterest#updatedAt
	 **/
	public static volatile SingularAttribute<CityEventInterest, Instant> updatedAt;

}

