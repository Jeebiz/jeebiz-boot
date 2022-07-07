/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import com.github.hiwepy.ip2region.spring.boot.IP2regionTemplate;
import io.hiwepy.boot.autoconfigure.region.BaiduRegionTemplate;
import io.hiwepy.boot.autoconfigure.region.NestedRegionTemplate;
import io.hiwepy.boot.autoconfigure.region.PconlineRegionTemplate;
import io.hiwepy.boot.autoconfigure.weather.WeatherTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import okhttp3.OkHttpClient;
import org.springframework.data.redis.core.RedisOperationTemplate;

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

	@Bean
	public NestedRegionTemplate nestedRegionTemplate(RedisOperationTemplate redisOperation, IP2regionTemplate ip2RegionTemplate,
													 PconlineRegionTemplate pconlineRegionTemplate) {
		return new NestedRegionTemplate( redisOperation, ip2RegionTemplate, pconlineRegionTemplate);
	}

	@Bean
	public WeatherTemplate weatherTemplate(OkHttpClient okHttpClient) {
		return new WeatherTemplate(okHttpClient);
	}

}
