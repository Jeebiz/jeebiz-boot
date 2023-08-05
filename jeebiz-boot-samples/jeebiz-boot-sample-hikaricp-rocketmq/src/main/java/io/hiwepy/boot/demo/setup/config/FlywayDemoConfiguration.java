/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.demo.setup.config;

import org.flywaydb.spring.boot.ext.FlywayFluentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayDemoConfiguration {
	
	@Bean
	public FlywayFluentConfiguration flywayFluentConfiguration() {
		
		FlywayFluentConfiguration configuration = new FlywayFluentConfiguration("demo",
				"Demo-模块初始化", "1.0.0");
		
		return configuration;
	}
	
}
