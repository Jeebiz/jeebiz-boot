/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.biz.web.client.RestTemplateResponseErrorHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

import com.google.common.collect.Lists;

import net.jeebiz.boot.autoconfigure.JeebizRestTemplateProperties.ClientType;

public class JeebizRestTemplateConfiguration {

	@Bean
	@ConditionalOnMissingBean
    public ResponseErrorHandler responseErrorHandler() {
		try {
			return new RestTemplateResponseErrorHandler();
		} catch (Exception e) {
			return new DefaultResponseErrorHandler();
		}
	}
	
	@Bean
	@ConditionalOnMissingBean
    public UriTemplateHandler uriTemplateHandler() {
		try {
			return new DefaultUriBuilderFactory();
		} catch (Exception e) {
			return new DefaultUriBuilderFactory();
		}
	}
	
	/**
	 * https://segmentfault.com/a/1190000010754013
	 * https://www.cnblogs.com/ssslinppp/p/8036603.html
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	@Bean
	@ConditionalOnMissingBean
	public RestTemplate restTemplate(@Autowired(required = false) List<ClientHttpRequestInterceptor> interceptors,
			RestTemplateBuilder restTemplateBuilder, 
			ResponseErrorHandler responseErrorHandler,
			UriTemplateHandler uriTemplateHandler,
			ObjectProvider<ClientHttpRequestFactory> clientHttpRequestFactorys,
			JeebizRestTemplateProperties properties)
			throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		
		/*
		restTemplateBuilder
				.additionalInterceptors(interceptors)
				.defaultMessageConverters()
				.errorHandler(responseErrorHandler)
				.setConnectTimeout(100000)
				.uriTemplateHandler(uriTemplateHandler)
				.build();
		 */

		ClientHttpRequestFactory clientHttpRequestFactory = null;
		if (ClientType.OKHTTP3.compareTo(properties.getClientType()) == 0) {
			clientHttpRequestFactory = clientHttpRequestFactorys.stream()
					.filter(client -> OkHttp3ClientHttpRequestFactory.class.isAssignableFrom(client.getClass()))
					.findFirst().get();
		} else if (ClientType.HTTP_COMPONENTS.compareTo(properties.getClientType()) == 0) {
			clientHttpRequestFactory = clientHttpRequestFactorys.stream()
					.filter(client -> HttpComponentsClientHttpRequestFactory.class.isAssignableFrom(client.getClass()))
					.findFirst().get();
		} else {
			clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		}
		
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		restTemplate.setErrorHandler(responseErrorHandler);
		restTemplate.setInterceptors(interceptors == null ? Lists.newArrayList() : interceptors);
		return restTemplate;
	}
	
	
}
