/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package net.jeebiz.boot.autoconfigure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.biz.context.SpringContextAwareContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@MapperScan({"net.jeebiz.**.dao", "net.jeebiz.**repository"})
@ComponentScan(basePackages = {"net.jeebiz.**.setup", "net.jeebiz.**.service", "net.jeebiz.**.aspect", "net.jeebiz.**.task", "net.jeebiz.**.extras"})
public class DefaultSpringContextConfiguration {

	@Bean
	public SpringContextAwareContext springContextAwareContext() {
		return new SpringContextAwareContext();
	}

}


