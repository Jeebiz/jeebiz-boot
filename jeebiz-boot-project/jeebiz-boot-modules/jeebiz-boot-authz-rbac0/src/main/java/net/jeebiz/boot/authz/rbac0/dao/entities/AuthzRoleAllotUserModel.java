/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.dao.entities;

import java.util.List;

import org.apache.ibatis.type.Alias;

import net.jeebiz.boot.api.dao.entities.PaginationModel;

@Alias(value = "AuthzRoleAllotUserModel")
@SuppressWarnings("serial")
public class AuthzRoleAllotUserModel extends PaginationModel {

	/**
	 * 角色ID
	 */
	private String roleId;
	/**
	 * 用户ID集合
	 */
	private List<String> userIds;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}

}
