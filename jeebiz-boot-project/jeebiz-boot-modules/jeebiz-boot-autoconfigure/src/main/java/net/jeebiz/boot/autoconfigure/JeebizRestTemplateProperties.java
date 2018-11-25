package net.jeebiz.boot.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(JeebizRestTemplateProperties.PREFIX)
public class JeebizRestTemplateProperties {

	public static final String PREFIX = "jeebiz.rest-template";
	
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
