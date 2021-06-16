/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.biz.web.server.ReactiveRequestContextFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.reactive.config.DelegatingWebFluxConfiguration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.jeebiz.boot.autoconfigure.config.LocalResourceProperteis;
import net.jeebiz.boot.autoconfigure.jackson.MyBeanSerializerModifier;
import net.jeebiz.boot.autoconfigure.webflux.DefaultExceptinHandler;
import net.jeebiz.boot.autoconfigure.webflux.GlobalErrorWebExceptionHandler;

@Configuration
@ComponentScan(basePackages = { "net.jeebiz.**.flux", "net.jeebiz.**.web", "net.jeebiz.**.route" })
@EnableWebFlux
@EnableConfigurationProperties(LocalResourceProperteis.class)
public class DefaultWebFluxConfiguration extends DelegatingWebFluxConfiguration {

	@Bean
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	public ReactiveRequestContextFilter requestContextFilter() {
		return new ReactiveRequestContextFilter();
	}

	@Bean
	public DefaultExceptinHandler defaultExceptinHandler() {
		return new DefaultExceptinHandler();
	}

	@Bean
	@Order(-2)
	public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
			Resources resources, ObjectProvider<ViewResolver> viewResolvers,
			ServerCodecConfigurer serverCodecConfigurer, ApplicationContext applicationContext) {
		GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(errorAttributes,
				resources,  applicationContext);
		exceptionHandler.setViewResolvers(viewResolvers.orderedStream().collect(Collectors.toList()));
		exceptionHandler.setMessageWriters(serverCodecConfigurer.getWriters());
		exceptionHandler.setMessageReaders(serverCodecConfigurer.getReaders());
		return exceptionHandler;
	}
	
	@Override
	protected LocaleContextResolver createLocaleContextResolver() {
		
		AcceptHeaderLocaleContextResolver localeContextResolver = new AcceptHeaderLocaleContextResolver();
		localeContextResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);

		return localeContextResolver;
	}
	
	@Bean
	public DefaultWebFluxConfigurer defaultWebFluxConfigurer(LocalResourceProperteis localResourceProperteis) {
		return new DefaultWebFluxConfigurer(localResourceProperteis);
	}
	
	@Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		
		ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
		//objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
		 
        /** 为objectMapper注册一个带有SerializerModifier的Factory */
        objectMapper.setSerializerFactory(objectMapper.getSerializerFactory()
                .withSerializerModifier(new MyBeanSerializerModifier()));

        //SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        //serializerProvider.setNullValueSerializer(NullObjectJsonSerializer.INSTANCE);
		
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

}
