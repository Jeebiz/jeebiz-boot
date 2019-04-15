/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AuthzUserVo", description = "用户信息参数Vo")
public class AuthzUserVo {

	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "id", dataType = "String", notes = "用户ID")
	private String id;
	/**
	 * 用户别名（昵称）
	 */
	@ApiModelProperty(value = "alias", required = true, dataType = "String", notes = "用户昵称")
	private String alias;
	/**
	 * 用户头像：图片路径或图标样式
	 */
	@ApiModelProperty(value = "avatar", dataType = "String", notes = "用户头像：图片路径或图标样式")
	private String avatar;
	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "username", required = true, dataType = "String", notes = "用户名")
	private String username;
	/**
	 * 用户密码
	 */
	private String password;
	/**
	 * 用户密码盐：用于密码加解密
	 */
	private String salt;
	/**
	 * 用户秘钥：用于用户JWT加解密
	 */
	private String secret;
	/**
	 * 手机号码
	 */
	private String phone;
	/**
	 * 电子邮箱
	 */
	private String email;
	/**
	 * 用户备注
	 */
	private String remark;
	/**
	 * 用户状态（0:禁用|1:可用|2:锁定）
	 */
	private String status;
	/**
	 * 角色ID
	 */
	private String roleId;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getTime24() {
		return time24;
	}

	public void setTime24(String time24) {
		this.time24 = time24;
	}

}
