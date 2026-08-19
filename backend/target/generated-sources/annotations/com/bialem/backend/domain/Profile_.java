package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.ProfileStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Profile.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Profile_ {

	public static final String PREFERENCES = "preferences";
	public static final String AVATAR_URL = "avatarUrl";
	public static final String CITY = "city";
	public static final String IS_VERIFIED = "isVerified";
	public static final String DISPLAY_NAME = "displayName";
	public static final String BIO = "bio";
	public static final String CREATED_AT = "createdAt";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String USERNAME = "username";
	public static final String STATUS = "status";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.Profile#preferences
	 **/
	public static volatile SingularAttribute<Profile, AccountPreferences> preferences;
	
	/**
	 * @see com.bialem.backend.domain.Profile#avatarUrl
	 **/
	public static volatile SingularAttribute<Profile, String> avatarUrl;
	
	/**
	 * @see com.bialem.backend.domain.Profile#city
	 **/
	public static volatile SingularAttribute<Profile, String> city;
	
	/**
	 * @see com.bialem.backend.domain.Profile#isVerified
	 **/
	public static volatile SingularAttribute<Profile, Boolean> isVerified;
	
	/**
	 * @see com.bialem.backend.domain.Profile#displayName
	 **/
	public static volatile SingularAttribute<Profile, String> displayName;
	
	/**
	 * @see com.bialem.backend.domain.Profile#bio
	 **/
	public static volatile SingularAttribute<Profile, String> bio;
	
	/**
	 * @see com.bialem.backend.domain.Profile#createdAt
	 **/
	public static volatile SingularAttribute<Profile, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Profile#id
	 **/
	public static volatile SingularAttribute<Profile, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Profile
	 **/
	public static volatile EntityType<Profile> class_;
	
	/**
	 * @see com.bialem.backend.domain.Profile#user
	 **/
	public static volatile SingularAttribute<Profile, User> user;
	
	/**
	 * @see com.bialem.backend.domain.Profile#username
	 **/
	public static volatile SingularAttribute<Profile, String> username;
	
	/**
	 * @see com.bialem.backend.domain.Profile#status
	 **/
	public static volatile SingularAttribute<Profile, ProfileStatus> status;
	
	/**
	 * @see com.bialem.backend.domain.Profile#updatedAt
	 **/
	public static volatile SingularAttribute<Profile, Instant> updatedAt;

}

