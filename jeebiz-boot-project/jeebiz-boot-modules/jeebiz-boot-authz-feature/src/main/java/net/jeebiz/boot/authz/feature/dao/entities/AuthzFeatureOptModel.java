package net.jeebiz.boot.authz.feature.dao.entities;

import org.apache.ibatis.type.Alias;

import net.jeebiz.boot.api.dao.entities.BaseModel;

@SuppressWarnings("serial")
@Alias(value = "AuthzFeatureOptModel")
public class AuthzFeatureOptModel extends BaseModel {

	/**
	 * 功能菜单ID
	 */
	private String featureId;
	/**
	 * 功能操作名称
	 */
	private String name;
	/**
	 * 功能操作图标样式
	 */
	private String icon;
	/**
	 * 功能操作排序
	 */
	private String order;
	/**
	 * 功能操作是否可见(1:可见|0:不可见)
	 */
	private String visible;
	/**
	 * 功能操作是否授权(1:已授权|0:未授权)
	 */
	private String checked;
	/**
	 * 功能操作权限标记
	 */
	private String perms;

	public String getFeatureId() {
		return featureId;
	}

	public void setFeatureId(String featureId) {
		this.featureId = featureId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}
	
}
