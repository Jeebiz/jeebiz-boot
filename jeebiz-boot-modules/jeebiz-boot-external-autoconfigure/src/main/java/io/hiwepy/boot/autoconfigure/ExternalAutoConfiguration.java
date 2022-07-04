/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import io.hiwepy.boot.autoconfigure.region.BaiduRegionTemplate;
import io.hiwepy.boot.autoconfigure.region.PconlineRegionTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;

/**
 */
@Configuration
@EnableConfigurationProperties({ ExternalProperties.class })
public class ExternalAutoConfiguration {
	 
	@Bean
	public BaiduRegionTemplate baiduRegionTemplate(ExternalProperties properties, OkHttpClient okHttpClient) {
		return new BaiduRegionTemplate(properties.getBaiduAk(), okHttpClient);
	}
 
	@Bean
	public PconlineRegionTemplate pconlineRegionTemplate(OkHttpClient okHttpClient) {
		return new PconlineRegionTemplate(okHttpClient);
	}
	
}