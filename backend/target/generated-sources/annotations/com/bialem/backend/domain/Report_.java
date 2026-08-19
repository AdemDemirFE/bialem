package com.bialem.backend.domain;

import com.bialem.backend.domain.enumeration.ReportStatus;
import com.bialem.backend.domain.enumeration.ReportTargetType;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(Report.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Report_ {

	public static final String REASON = "reason";
	public static final String CREATED_AT = "createdAt";
	public static final String RESOLVED_AT = "resolvedAt";
	public static final String TARGET_ID = "targetId";
	public static final String RESOLVED_BY = "resolvedBy";
	public static final String TARGET_TYPE = "targetType";
	public static final String DETAILS = "details";
	public static final String REPORTER = "reporter";
	public static final String ID = "id";
	public static final String STATUS = "status";
	public static final String UPDATED_AT = "updatedAt";

	
	/**
	 * @see com.bialem.backend.domain.Report#reason
	 **/
	public static volatile SingularAttribute<Report, String> reason;
	
	/**
	 * @see com.bialem.backend.domain.Report#createdAt
	 **/
	public static volatile SingularAttribute<Report, Instant> createdAt;
	
	/**
	 * @see com.bialem.backend.domain.Report#resolvedAt
	 **/
	public static volatile SingularAttribute<Report, Instant> resolvedAt;
	
	/**
	 * @see com.bialem.backend.domain.Report#targetId
	 **/
	public static volatile SingularAttribute<Report, String> targetId;
	
	/**
	 * @see com.bialem.backend.domain.Report#resolvedBy
	 **/
	public static volatile SingularAttribute<Report, Profile> resolvedBy;
	
	/**
	 * @see com.bialem.backend.domain.Report#targetType
	 **/
	public static volatile SingularAttribute<Report, ReportTargetType> targetType;
	
	/**
	 * @see com.bialem.backend.domain.Report#details
	 **/
	public static volatile SingularAttribute<Report, String> details;
	
	/**
	 * @see com.bialem.backend.domain.Report#reporter
	 **/
	public static volatile SingularAttribute<Report, Profile> reporter;
	
	/**
	 * @see com.bialem.backend.domain.Report#id
	 **/
	public static volatile SingularAttribute<Report, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.Report
	 **/
	public static volatile EntityType<Report> class_;
	
	/**
	 * @see com.bialem.backend.domain.Report#status
	 **/
	public static volatile SingularAttribute<Report, ReportStatus> status;
	
	/**
	 * @see com.bialem.backend.domain.Report#updatedAt
	 **/
	public static volatile SingularAttribute<Report, Instant> updatedAt;

}

