/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.dao.entities;

/**
 * 用户model对象，用户信息顶层model
 * @author wandl
 */
@SuppressWarnings("serial")
public class UserModel extends BaseModel<UserModel> {

	/**
	 * 用户ID（用户来源表Id）
	 */
	protected String userid;
	/**
	 * 用户Key
	 */
	protected String userkey;
	/**
	 * 用户名称
	 */
	protected String username;
	/**
	 * 用户密码
	 */
	protected String password;
	/**
	 * 用户类型;1:超级管理员，2：普通管理员，等（根据系统业务自定义）
	 */
	protected String usertype;
	/**
	 * 固定联系电话
	 */
	protected String telephone;
	/**
	 * 移动电话
	 */
	protected String mobile;
	/**
	 * 电子邮箱
	 */
	protected String email;
	/**
	 * 腾讯QQ
	 */
	protected String qq;
	/**
	 * 个人主页地址
	 */
	protected String homepage;
	/**
	 * 是否禁用;false：解禁，true：禁用
	 */
	protected Boolean disabled = Boolean.FALSE;
	/**
	 * 是否锁定;false：解锁，true：锁定
	 */
    protected Boolean locked = Boolean.FALSE;
    
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUserkey() {
		return userkey;
	}

	public void setUserkey(String userkey) {
		this.userkey = userkey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
 
	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}
	
}
