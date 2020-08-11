/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;

import net.jeebiz.boot.autoconfigure.config.LocalResourceProperteis;
import net.jeebiz.boot.autoconfigure.webflux.ReactiveRequestContextFilter;

@Configuration
@ComponentScan(basePackages = { "net.jeebiz.**.flux", "net.jeebiz.**.web", "net.jeebiz.**.route" })
@EnableConfigurationProperties(LocalResourceProperteis.class)
public class DefaultWebFluxConfiguration extends DelegatingWebFluxConfiguration {

	@Bean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	public ReactiveRequestContextFilter requestContextFilter() {
		return new ReactiveRequestContextFilter();
	}

	@Override
	protected LocaleContextResolver createLocaleContextResolver() {
		

		AcceptHeaderLocaleContextResolver localeContextResolver = new AcceptHeaderLocaleContextResolver() {
			
			@Override
			public void setLocaleContext(ServerWebExchange exchange, LocaleContext localeContext) {
				if(localeContext != null) {
					LocaleContextHolder.setLocaleContext(localeContext);
				}
			}
			
		};
		
		localeContextResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);

		return localeContextResolver;
	}
	
	@Bean
	public DefaultWebFluxConfigurer defaultWebFluxConfigurer(LocalResourceProperteis localResourceProperteis) {
		return new DefaultWebFluxConfigurer(localResourceProperteis);
	}

}
