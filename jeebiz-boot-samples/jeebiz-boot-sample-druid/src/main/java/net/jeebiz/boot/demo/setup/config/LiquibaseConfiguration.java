/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.setup.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;

import liquibase.integration.spring.SpringLiquibase;

/**
 * https://segmentfault.com/a/1190000016641122?utm_source=tag-newest 
 */
@Configuration
public class LiquibaseConfiguration {

    /**
     *  用户模块Liquibase   
     */
    @Bean
    public SpringLiquibase userLiquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        // 用户模块Liquibase文件路径
        liquibase.setChangeLog("classpath:liquibase/user/master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setShouldRun(true);
        liquibase.setResourceLoader(new DefaultResourceLoader());
        // 覆盖Liquibase changelog表名
        liquibase.setDatabaseChangeLogTable("user_changelog_table");
        liquibase.setDatabaseChangeLogLockTable("user_changelog_lock_table");
        return liquibase;
    }
    
    /**
     *  订单模块Liquibase   
     */
    @Bean
    public SpringLiquibase orderLiquibase(DataSource dataSource) {
      SpringLiquibase liquibase = new SpringLiquibase();
      liquibase.setChangeLog("classpath:liquibase/order/master.xml");
      liquibase.setDataSource(dataSource);
      liquibase.setShouldRun(true);
      liquibase.setResourceLoader(new DefaultResourceLoader());
      liquibase.setDatabaseChangeLogTable("order_changelog_table");
      liquibase.setDatabaseChangeLogLockTable("order_changelog_lock_table");
      return liquibase;
    }
}