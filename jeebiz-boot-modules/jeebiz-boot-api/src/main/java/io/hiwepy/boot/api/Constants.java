/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constants {

    public static Marker accessMarker = MarkerFactory.getMarker("io.hiwepy.access");
    public static Marker authzMarker = MarkerFactory.getMarker("io.hiwepy.authz");
    public static Marker bizMarker = MarkerFactory.getMarker("io.hiwepy.biz");

    public static final String RT_SUCCESS = "success";
    public static final String RT_FAIL = "fail";
    public static final String RT_ERROR = "error";
    public static final String THEME_PARAM_NAME = "theme";
    public static final String THEME_PARAM_DEFAULT = "default";
    public static final String THEME_SOURCE_CLASSPATH = "classpath:/static/assets/css/themes/";


    public static final String LANG_PARAM_NAME = "lang";

}

