/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.dao.entities;

import org.apache.ibatis.type.Alias;

import net.jeebiz.boot.api.dao.entities.PaginationModel;

@Alias(value = "AuthzUserDetailModel")
@SuppressWarnings("serial")
public class AuthzUserDetailModel extends PaginationModel {

	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 用户别名（昵称）
	 */
	private String alias;
	/**
	 * 密码
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
	 * 用户头像：图片路径或图标样式
	 */
	private String avatar;
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
	 * 初始化时间
	 */
	private String time24;

	/**
	 * 性别：（male：男，female：女）
	 */
	private String gender;
	/**
	 * 出生日期
	 */
	private String birthday;
	/**
	 * 身份证号码
	 */
	private String idcard;
	/**
	 * 角色ID
	 */
	private String roleId;
	
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getTime24() {
		return time24;
	}

	public void setTime24(String time24) {
		this.time24 = time24;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
}
