/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.extras.external.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(ExternalProperties.PREFIX)
@Data
public class ExternalProperties {

	public static final String PREFIX = "external";
	
	private String baiduAk;

}
