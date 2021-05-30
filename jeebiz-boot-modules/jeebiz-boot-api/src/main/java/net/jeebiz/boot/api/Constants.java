/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api;

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
	
}

