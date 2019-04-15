/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.autoconfigure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 软件服务供应商信息
 */
@ConfigurationProperties("server.vendor")
public class ServiceVendorProperties {
	
	/**
	 * 省份
	 */
	private String province;

	/**
	 * 城市
	 */
	private String city;

	/**
	 * 描述
	 */
	private String desc;
	/**
	 * 版权信息
	 */
	private String copyright;

	/**
	 * IPC
	 */
	private String ipcLicense;
	/**
	 * 企业名称
	 */
	private String company;
	/**
	 * 地址
	 */
	private String addr;
	/**
	 * 电话
	 */
	private String tel;
	/**
	 * 电子邮箱
	 */
	private String email;
	/**
	 * 传真
	 */
	private String fax;
	/**
	 * 系统名称
	 */
	private String title;
	

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getIpcLicense() {
		return ipcLicense;
	}

	public void setIpcLicense(String ipcLicense) {
		this.ipcLicense = ipcLicense;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "ServiceVendor{" + "province='" + province + '\'' + ", city='" + city + '\'' + ", desc='" + desc + '\''
				+ '}';
	}
}
