/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

@ConfigurationProperties(prefix = "spring.storage")
public class LocalResourceProperteis {

    // 本地存储路径
    private String localStorage;
    
    // 本地静态资源映射是否是相对于localStorage的地址
    private boolean localRelative;

    // 本地静态资源映射
    private Map<String, String> localLocations = Maps.newHashMap();

	public String getLocalStorage() {
		return localStorage;
	}

	public void setLocalStorage(String localStorage) {
		this.localStorage = localStorage;
	}

	public boolean isLocalRelative() {
		return localRelative;
	}

	public void setLocalRelative(boolean localRelative) {
		this.localRelative = localRelative;
	}

	public Map<String, String> getLocalLocations() {
		return localLocations;
	}

	public void setLocalLocations(Map<String, String> localLocations) {
		this.localLocations = localLocations;
	}
    
}
