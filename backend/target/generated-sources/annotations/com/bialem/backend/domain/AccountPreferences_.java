package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.AllowMessagesFrom;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(AccountPreferences.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class AccountPreferences_ {

	public static final String PROFILE = "profile";
	public static final String REQUIRE_FOLLOW_APPROVAL = "requireFollowApproval";
	public static final String NOTIFY_ADVANTAGES = "notifyAdvantages";
	public static final String NOTIFY_EVENTS = "notifyEvents";
	public static final String ALLOW_MESSAGES_FROM = "allowMessagesFrom";
	public static final String SHOW_CITY = "showCity";
	public static final String NOTIFY_COMMUNITIES = "notifyCommunities";
	public static final String SHOW_FOLLOW_CONNECTIONS = "showFollowConnections";
	public static final String DISCOVERABLE = "discoverable";
	public static final String ALLOW_FOLLOWS = "allowFollows";
	public static final String NOTIFY_SOCIAL = "notifySocial";
	public static final String NOTIFY_SYSTEM = "notifySystem";
	public static final String ID = "id";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#profile
	 **/
	public static volatile SingularAttribute<AccountPreferences, Profile> profile;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#requireFollowApproval
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> requireFollowApproval;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#notifyAdvantages
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> notifyAdvantages;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#notifyEvents
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> notifyEvents;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#allowMessagesFrom
	 **/
	public static volatile SingularAttribute<AccountPreferences, AllowMessagesFrom> allowMessagesFrom;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#showCity
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> showCity;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#notifyCommunities
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> notifyCommunities;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#showFollowConnections
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> showFollowConnections;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#discoverable
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> discoverable;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#allowFollows
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> allowFollows;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#notifySocial
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> notifySocial;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#notifySystem
	 **/
	public static volatile SingularAttribute<AccountPreferences, Boolean> notifySystem;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#id
	 **/
	public static volatile SingularAttribute<AccountPreferences, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences
	 **/
	public static volatile EntityType<AccountPreferences> class_;
	
	/**
	 * @see com.bialem.backend.domain.AccountPreferences#updatedAt
	 **/
	public static volatile SingularAttribute<AccountPreferences, Instant> updatedAt;

}

