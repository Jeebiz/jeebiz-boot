package net.jeebiz.boot.authz.feature.setup.config;

import org.flywaydb.spring.boot.ext.FlywayClassicConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureConfiguration {
	
	@Bean
	public FlywayClassicConfiguration flywayFeatureConfiguration() {
		
		FlywayClassicConfiguration configuration = new FlywayClassicConfiguration("feature",
				"功能菜单-模块初始化", "1.0.0");
		
		return configuration;
	}
	
}
