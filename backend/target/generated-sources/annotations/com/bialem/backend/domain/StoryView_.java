package com.bialem.backend.domain;

import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

@StaticMetamodel(StoryView.class)
public abstract class StoryView_ {

	public static final String VIEWER = "viewer";
	public static final String VIEWED_AT = "viewedAt";
	public static final String ID = "id";
	public static final String STORY = "story";

	
	/**
	 * @see com.bialem.backend.domain.StoryView#viewer
	 **/
	public static volatile SingularAttribute<StoryView, Profile> viewer;
	
	/**
	 * @see com.bialem.backend.domain.StoryView#viewedAt
	 **/
	public static volatile SingularAttribute<StoryView, Instant> viewedAt;
	
	/**
	 * @see com.bialem.backend.domain.StoryView#id
	 **/
	public static volatile SingularAttribute<StoryView, Long> id;
	
	/**
	 * @see com.bialem.backend.domain.StoryView
	 **/
	public static volatile EntityType<StoryView> class_;
	
	/**
	 * @see com.bialem.backend.domain.StoryView#story
	 **/
	public static volatile SingularAttribute<StoryView, Story> story;

}

