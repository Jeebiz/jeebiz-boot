/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.dao.entities;

import org.apache.ibatis.type.Alias;

import net.jeebiz.boot.api.dao.entities.BaseModel;

/**
 * 功能菜单-功能操作关系表
 */
@Alias(value = "AuthzRelationModel")
@SuppressWarnings("serial")
public class AuthzRelationModel extends BaseModel {

	/**
	 * 功能菜单ID
	 */
	private String mId;
	/**
	 * 功能操作代码
	 */
	private String code;
	/**
	 * 是否可见(1:可见|0:不可见)
	 */
	private String visible;
	/**
	 * 权限标记
	 */
	private String perms;
	/**
	 * 功能操作名称(用于覆盖功能操作表的操作名称)
	 */
	private String name;
	/**
	 * 显示顺序
	 */
	private String order;

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
