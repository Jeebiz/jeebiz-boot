/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.dao.entities;

public class ErrorPair {

	/**
	 * 错误编码
	 */
	private String key;
	/**
	 * 错误描述
	 */
	private String desc;

	public ErrorPair() {

	}
	public ErrorPair(String key, String desc) {
		this.key = key;
		this.desc = desc;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
