/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao.entities;

import com.baomidou.mybatisplus.extension.activerecord.Model;

@SuppressWarnings("serial")
public class BaseModel<T extends Model<?>> extends Model<T> implements Cloneable {
	
	/**
	 * 应用唯一ID
	 */
	private String appid;
	/**
	 * 国际化Local值，默认zh_CN,其他值如en_US,zh_CN
	 */
	private String locale = "zh_CN";
	/**
	 * 主键 
	 */ 
	private String id;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
