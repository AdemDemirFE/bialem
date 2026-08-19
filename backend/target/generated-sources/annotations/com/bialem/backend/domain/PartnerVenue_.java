package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.PartnerVenueCategory;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.Instant;

@StaticMetamodel(PartnerVenue.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class PartnerVenue_ {

	public static final String OFFERS = "offers";
	public static final String ADDRESS = "address";
	public static final String CITY = "city";
	public static final String COVER_IMAGE_URL = "coverImageUrl";
	public static final String LATITUDE = "latitude";
	public static final String DESCRIPTION = "description";
	public static final String STAFF = "staff";
	public static final String IS_ACTIVE = "isActive";
	public static final String LOGO_URL = "logoUrl";
	public static final String CREATED_AT = "createdAt";
	public static final String PHONE = "phone";
	public static final String WEBSITE_URL = "websiteUrl";
	public static final String NAME = "name";
	public static final String INSTAGRAM_URL = "instagramUrl";
	public static final String ID = "id";
	public static final String CATEGORY = "category";
	public static final String IS_FEATURED = "isFeatured";
	public static final String SLUG = "slug";
	public static final String LONGITUDE = "longitude";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#offers
	 **/
	public static volatile SetAttribute<PartnerVenue, PartnerOffer> offers;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#address
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> address;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#city
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> city;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#coverImageUrl
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> coverImageUrl;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#latitude
	 **/
	public static volatile SingularAttribute<PartnerVenue, BigDecimal> latitude;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#description
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> description;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#staff
	 **/
	public static volatile SetAttribute<PartnerVenue, PartnerVenueStaff> staff;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#isActive
	 **/
	public static volatile SingularAttribute<PartnerVenue, Boolean> isActive;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#logoUrl
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> logoUrl;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#createdAt
	 **/
	public static volatile SingularAttribute<PartnerVenue, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#phone
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> phone;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#websiteUrl
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> websiteUrl;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#name
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> name;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#instagramUrl
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> instagramUrl;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#id
	 **/
	public static volatile SingularAttribute<PartnerVenue, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#category
	 **/
	public static volatile SingularAttribute<PartnerVenue, PartnerVenueCategory> category;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#isFeatured
	 **/
	public static volatile SingularAttribute<PartnerVenue, Boolean> isFeatured;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue
	 **/
	public static volatile EntityType<PartnerVenue> class_;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#slug
	 **/
	public static volatile SingularAttribute<PartnerVenue, String> slug;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#longitude
	 **/
	public static volatile SingularAttribute<PartnerVenue, BigDecimal> longitude;
	
	/**
	 * @see com.bialem.backend.domain.PartnerVenue#updatedAt
	 **/
	public static volatile SingularAttribute<PartnerVenue, Instant> updatedAt;

}

