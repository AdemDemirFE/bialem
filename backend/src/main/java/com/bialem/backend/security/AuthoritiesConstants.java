package com.bialem.backend.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String SUPER_ADMIN = "ROLE_SUPER_ADMIN";

    public static final String COMMUNITY_MANAGER = "ROLE_COMMUNITY_MANAGER";

    public static final String EVENT_MANAGER = "ROLE_EVENT_MANAGER";

    public static final String MODERATOR = "ROLE_MODERATOR";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
