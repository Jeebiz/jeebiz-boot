/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.autoconfigure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务信息
 */
@ConfigurationProperties("server.info")
public class ServiceInfoProperties {

	/**
	 * 服务节点UID: spring-boot-admin 服务端注册ID
	 */
	private String uid;
	/**
	 * 服务节点名称
	 */
	private String name;

	/**
	 * 服务节点描述
	 */
	private String description;

	/**
	 * 服务节点版本
	 */
	private String version;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ServiceInfo{" + "name='" + name + '\'' + ", version='" + version + '\'' + '}';
	}
}
