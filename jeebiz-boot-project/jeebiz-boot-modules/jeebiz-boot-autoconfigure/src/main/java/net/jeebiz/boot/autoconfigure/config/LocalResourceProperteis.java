package net.jeebiz.boot.autoconfigure.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Maps;

@ConfigurationProperties(prefix = "spring.resources")
public class LocalResourceProperteis {

    // 本地存储路径
    private String localStorage;

    // 本地静态资源映射
    private Map<String, String> localLocations = Maps.newHashMap();

	public String getLocalStorage() {
		return localStorage;
	}

	public void setLocalStorage(String localStorage) {
		this.localStorage = localStorage;
	}

	public Map<String, String> getLocalLocations() {
		return localLocations;
	}

	public void setLocalLocations(Map<String, String> localLocations) {
		this.localLocations = localLocations;
	}
    
}
