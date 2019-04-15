/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.dao.entities;

import java.util.List;

import org.apache.ibatis.type.Alias;

import com.google.common.collect.Lists;

import net.jeebiz.boot.api.dao.entities.PaginationModel;

@Alias(value = "AuthzRoleModel")
@SuppressWarnings("serial")
public class AuthzRoleModel extends PaginationModel {

	/**
	 * 角色ID
	 */
	private String id;
	/**
	 * 角色名称
	 */
	private String name;
	/**
	 * 角色简介
	 */
	private String intro;
	/**
	 * 角色类型（1:原生|2:继承|3:复制|4:自定义）
	 */
	private String type;
	/**
	 * 角色状态（0:禁用|1:可用）
	 */
	private String status;
	/**
	 * 角色授权的标记集合
	 */
	private List<String> perms = Lists.newArrayList();
	/**
	 * 角色已分配用户量
	 */
	private int users;
	/**
	 * 初始化时间
	 */
	private String time24;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getPerms() {
		return perms;
	}

	public void setPerms(List<String> perms) {
		this.perms = perms;
	}

	public int getUsers() {
		return users;
	}

	public void setUsers(int users) {
		this.users = users;
	}

	public String getTime24() {
		return time24;
	}

	public void setTime24(String time24) {
		this.time24 = time24;
	}
	
}
