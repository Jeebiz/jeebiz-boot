/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(RestTemplateProperties.PREFIX)
public class RestTemplateProperties {

	public static final String PREFIX = "spring.template.rest";
	
	/**
	 * Client Type
	 */
	enum ClientType {
	
	    /**
	     * @see org.springframework.http.client.SimpleClientHttpRequestFactory
	     */
		SIMPLE,
		  /**
	     * @see org.springframework.http.client.HttpComponentsClientHttpRequestFactory
	     */
		HTTP_COMPONENTS,
		  /**
	     * @see org.springframework.http.client.OkHttp3ClientHttpRequestFactory
	     */
		OKHTTP3
	}
	
	/**
	 *  RestTemplate 底层客户端实现
	 */
	private ClientType clientType = ClientType.SIMPLE;

	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}
	
}
