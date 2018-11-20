package net.jeebiz.boot.authz.rbac0.setup.config;

import org.flywaydb.spring.boot.ext.FlywayFluentConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Rbac0Configuration {
	
	@Bean
	public FlywayFluentConfiguration flywayRbac0Configuration() {
		
		FlywayFluentConfiguration configuration = new FlywayFluentConfiguration("rbac0",
				"Rbac0权限管理-模块初始化", "1.0.0");
		
		return configuration;
	}
	
}
