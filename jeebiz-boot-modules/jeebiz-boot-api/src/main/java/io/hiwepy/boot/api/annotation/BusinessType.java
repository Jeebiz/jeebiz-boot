/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.annotation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 日志操作类型-枚举
 * @author <a href="https://github.com/wandl">wandl</a>
 */
public enum BusinessType {

	/**
	 * 用户登录
	 */
	LOGIN("login", "用户登录"),
	/**
	 * 用户注销
	 */
	LOGOUT("logout", "用户注销"),
	/**
	 * 数据写入
	 */
	INSERT("insert", "数据写入"),
	/**
	 * 数据刪除
	 */
	DELETE("delete", "数据刪除"),
	/**
	 * 数据更新
	 */
	UPDATE("update", "数据更新作"),
	/**
	 * 数据查询
	 */
	SELECT("select", "数据查询"),
	/**
	 * 文件上传
	 */
	UPLOAD("upload", "文件上传"),
	/**
	 * 文件下载
	 */
	DOWNLOAD("download", "文件下载"),
	/**
	 * 发送邮件
	 */
	EMAIL("email", "发送邮件"),
	/**
	 * 发送短信
	 */
	SMS("sms", "发送短信");
	
	private String key;
	private String desc;

	BusinessType(String key, String desc) {
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

	public boolean equals(BusinessType relation) {
		return this.compareTo(relation) == 0;
	}

	public static List<Map<String, String>> toList() {
		List<Map<String, String>> typeList = new LinkedList<Map<String, String>>();
		for (BusinessType typeEnum : BusinessType.values()) {
			typeList.add(typeEnum.toMap());
		}
		return typeList;
	}

	public Map<String, String> toMap() {
		Map<String, String> typeMap = new HashMap<String, String>();
		typeMap.put("key", this.getKey());
		typeMap.put("desc", this.getDesc());
		return typeMap;
	}
	
}
