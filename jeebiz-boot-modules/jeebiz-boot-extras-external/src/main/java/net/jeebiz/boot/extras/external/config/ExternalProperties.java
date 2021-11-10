/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.extras.external.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(ExternalProperties.PREFIX)
@Data
public class ExternalProperties {

	public static final String PREFIX = "external";
	
	private String baiduAk;

}
