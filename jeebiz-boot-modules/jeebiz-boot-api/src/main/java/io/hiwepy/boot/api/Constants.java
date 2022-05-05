/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constants {

	public static Marker authzMarker = MarkerFactory.getMarker("Authz-Log");
	public static Marker bizMarker = MarkerFactory.getMarker("Biz-Log");

	public static final String RT_SUCCESS = "success";
	public static final String RT_FAIL = "fail";
	public static final String RT_ERROR = "error";

}

