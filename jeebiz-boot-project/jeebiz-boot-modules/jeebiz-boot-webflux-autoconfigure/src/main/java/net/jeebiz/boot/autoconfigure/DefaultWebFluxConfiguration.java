/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.server.i18n.LocaleContextResolver;

@EnableWebFlux
@Configuration
@ComponentScan(basePackages = { "net.jeebiz.**.flux", "net.jeebiz.**.web", "net.jeebiz.**.route" })
public class DefaultWebFluxConfiguration  extends DelegatingWebFluxConfiguration  {
    
   @Override
	protected LocaleContextResolver createLocaleContextResolver() {
		// TODO Auto-generated method stub
		return super.createLocaleContextResolver();
	}
	
}
