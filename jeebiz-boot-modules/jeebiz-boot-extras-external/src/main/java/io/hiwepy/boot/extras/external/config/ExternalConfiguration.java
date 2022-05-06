/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.extras.external.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.hiwepy.boot.extras.external.region.BaiduRegionTemplate;
import io.hiwepy.boot.extras.external.region.PconlineRegionTemplate;
import okhttp3.OkHttpClient;

/**
 */
@Configuration
@EnableConfigurationProperties({ ExternalProperties.class })
public class ExternalConfiguration {
	 
	@Bean
	public BaiduRegionTemplate baiduRegionTemplate(ExternalProperties properties,OkHttpClient okHttpClient) {
		return new BaiduRegionTemplate(properties.getBaiduAk(), okHttpClient);
	}
 
	@Bean
	public PconlineRegionTemplate pconlineRegionTemplate(OkHttpClient okHttpClient) {
		return new PconlineRegionTemplate(okHttpClient);
	}
	
}