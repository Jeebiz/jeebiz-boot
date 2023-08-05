/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.demo.setup.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * 开启事务管理,确保在启动类中@component扫描到该类
 */
@Configuration
@EnableTransactionManagement 
public class DataSourceConfig {
	
	//注意: @Qualifier 按名称在IOC容器中找指定名称的bean，
	@Bean //或者 @Bean("myTransactionManager")
	public PlatformTransactionManager platformTransactionManager(
			@Qualifier("dataSource") DataSource myDataSource) {
		return new DataSourceTransactionManager(myDataSource);
	}
	
}
