/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@EnableWebFlux
@Configuration
@ComponentScan(basePackages = { "net.jeebiz.**.flux", "net.jeebiz.**.web", "net.jeebiz.**.route" })
public class DefaultWebFluxConfiguration implements WebFluxConfigurer {
    
    @Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		/*
		 * if (!registry.hasMappingForPattern("/webjars/**")) {
		 * registry.addResourceHandler("/webjars/**").addResourceLocations(
		 * "classpath:/META-INF/resources/webjars/"); } if
		 * (!registry.hasMappingForPattern("/**")) {
		 * 
		 * registry.addResourceHandler("/**").addResourceLocations( RESOURCE_LOCATIONS);
		 * 
		 * }
		 */

		/*
		 * registry.addResourceHandler("/webjars/**").addResourceLocations("/webjars/")
		 * .resourceChain(false) .addResolver(new WebJarsResourceResolver())
		 * .addResolver(new PathResourceResolver());
		 */

		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/assets/");
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
}
