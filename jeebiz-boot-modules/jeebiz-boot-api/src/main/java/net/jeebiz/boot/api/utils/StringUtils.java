/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.utils;


public class StringUtils extends org.springframework.util.StringUtils {

	/**
	 * Any number of these characters are considered delimiters between multiple
	 * context config paths in a single String value.
	 */
	public static String CONFIG_LOCATION_DELIMITERS = ",; \t\n";
	
	public static String[] tokenizeToStringArray(String str) {
		return tokenizeToStringArray(str, CONFIG_LOCATION_DELIMITERS);
	}
	
}
