package net.jeebiz.boot.authz.rbac0.setup.config;

import org.flywaydb.spring.boot.ext.FlywayClassicConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.jeebiz.boot.authz.feature.setup.handler.FeatureFlatDataHandler;
import net.jeebiz.boot.authz.feature.setup.handler.FeatureTreeDataHandler;

@Configuration
public class Rbac0Configuration {
	
	@Bean
	public FlywayClassicConfiguration flywayRbac0Configuration() {
		
		FlywayClassicConfiguration configuration = new FlywayClassicConfiguration("rbac0",
				"Rbac0权限管理-模块初始化", "1.0.0");
		
		return configuration;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public FeatureFlatDataHandler featureFlatDataHandler() {
		return new FeatureFlatDataHandler();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public FeatureTreeDataHandler featureTreeDataHandler() {
		return new FeatureTreeDataHandler();
	}
	
}
