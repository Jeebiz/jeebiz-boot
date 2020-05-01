/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;

import java.util.regex.Pattern;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constants {
	
	public static Marker authzMarker = MarkerFactory.getMarker("Authz-Log");
	public static Marker bizMarker = MarkerFactory.getMarker("Biz-Log");
	
	public static Pattern pattern_find = Pattern.compile("(?:(?:\\#\\{)([\\S]*?)(?:\\}))+", Pattern.CASE_INSENSITIVE);
	public static final String UID = "uid";
	public static final String UKEY = "ukey";
	public static final String UCODE = "ucode";
	public static final String RID = "rid";
	public static final String RKEY = "rkey";
	
	public static final String RT_SUCCESS = "success";
	public static final String RT_FAIL = "fail";
	public static final String RT_ERROR = "error";
	
	public static final String AUTHZ_FEATURE = "认证授权-菜单";
	public static final String AUTHZ_FEATURE_OPT = "认证授权-菜单-操作";
	public static final String AUTHZ_LOGIN = "认证授权-登录";
	public static final String AUTHZ_ROLE = "认证授权-角色";
	public static final String AUTHZ_ROLE_PERMS = "认证授权-角色-权限";
	public static final String AUTHZ_USER = "认证授权-用户";
	public static final String AUTHZ_USER_PERMS = "认证授权-用户-权限";

	public static final String PERMS_CACHE_KEY = "authz-dbperms";
	public static final String PERMS_CACHE_HASH = "dbperms:%s:%s";
	
}

