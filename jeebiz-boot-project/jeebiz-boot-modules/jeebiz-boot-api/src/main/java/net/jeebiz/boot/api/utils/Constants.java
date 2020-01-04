/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constants {
	
	public static Marker authzMarker = MarkerFactory.getMarker("Authz-Log");
	public static Marker bizMarker = MarkerFactory.getMarker("Biz-Log");
	
	public static final String RT_SUCCESS = "success";
	public static final String RT_FAIL = "fail";
	public static final String RT_ERROR = "error";
	
	public static final String AUTHZ_FEATURE = "Authz-Feature";
	public static final String AUTHZ_FEATURE_CACHE = "Authz-Feature-Cache";
	public static final String AUTHZ_FEATURE_OPT = "Authz-Feature-Opt";
	public static final String AUTHZ_LOGIN = "Authz-Login";
	public static final String AUTHZ_ROLE = "Authz-Role";
	public static final String AUTHZ_ROLE_PERMS = "Authz-Role-Perms";
	public static final String AUTHZ_USER = "Authz-User";
	public static final String AUTHZ_USER_PERMS = "Authz-User-Perms";

	public static final String PERMS_CACHE_KEY = "authz-dbperms";
	public static final String PERMS_CACHE_HASH = "dbperms:%s:%s";
	
}

