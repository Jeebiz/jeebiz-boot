package net.jeebiz.boot.authz.feature.web.vo;

import java.util.List;

import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModel;

@SuppressWarnings("rawtypes")
@ApiModel(value = "AuthzFeatureVo", description = "功能信息Vo")
public class AuthzFeatureVo implements Comparable<AuthzFeatureVo>{

	/**
	 * 功能菜单ID
	 */
	private String id;
	/**
	 * 功能菜单名称
	 */
	private String name;
	/**
	 * 功能菜单界面显示标签
	 */
	private String label;
	/**
	 * 功能菜单简称
	 */
	private String abb;
	/**
	 * 功能菜单编码：用于与功能操作代码组合出权限标记以及作为前段判断的依据
	 */
	private String code;
	/**
	 * 功能菜单URL
	 */
	private String url;
	/**
	 * 菜单类型(1:原生|2:自定义)
	 */
	private String type;
	/**
	 * 菜单样式或菜单图标路径
	 */
	private String icon;
	/**
	 * 菜单显示顺序
	 */
	private String order;
	/**
	 * 父级功能菜单ID
	 */
	private String parent;
	/**
	 * 菜单是否可见(1:可见|0:不可见)
	 */
	private String visible;
	/**
	 * 菜单所拥有的权限标记
	 */
	private String perms;
	/**
	 * 子菜单/功能按钮
	 */
	private List children = Lists.newArrayList();

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAbb() {
		return abb;
	}

	public void setAbb(String abb) {
		this.abb = abb;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
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

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}
	
	@Override
	public int compareTo(AuthzFeatureVo o) {
		return order.compareTo(o.getOrder());
	}

}
