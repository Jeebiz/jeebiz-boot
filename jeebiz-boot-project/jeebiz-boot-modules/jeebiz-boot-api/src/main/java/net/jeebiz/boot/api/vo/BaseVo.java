/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.vo;

import io.swagger.annotations.ApiModelProperty;

public abstract class BaseVo {

	/**
	 * 应用唯一ID
	 */
	@ApiModelProperty(value = "appid", dataType = "String", notes = "应用唯一ID")
	private String appid;
	/**
	 * 主键ID
	 */
	@ApiModelProperty(value = "id", dataType = "String", notes = "主键ID")
	private String id;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}