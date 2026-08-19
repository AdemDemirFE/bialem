package com.bialem.backend.domain;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(CityEventSyncLog.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class CityEventSyncLog_ {

	public static final String PROVIDER_CODE = "providerCode";
	public static final String ERROR_MESSAGE = "errorMessage";
	public static final String STARTED_AT = "startedAt";
	public static final String ID = "id";
	public static final String IMPORTED_COUNT = "importedCount";
	public static final String STATUS = "status";
	public static final String FINISHED_AT = "finishedAt";

	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#providerCode
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, String> providerCode;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#errorMessage
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, String> errorMessage;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#startedAt
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, Instant> startedAt;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#id
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#importedCount
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, Integer> importedCount;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog
	 **/
	public static volatile EntityType<CityEventSyncLog> class_;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#status
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, String> status;
	
	/**
	 * @see com.bialem.backend.domain.CityEventSyncLog#finishedAt
	 **/
	public static volatile SingularAttribute<CityEventSyncLog, Instant> finishedAt;

}

