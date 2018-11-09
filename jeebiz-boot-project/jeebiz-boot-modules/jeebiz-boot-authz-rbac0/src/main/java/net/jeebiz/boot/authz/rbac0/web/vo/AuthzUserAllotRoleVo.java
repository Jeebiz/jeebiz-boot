package net.jeebiz.boot.authz.rbac0.web.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AuthzUserAllotRoleVo", description = "用户分配角色参数Vo")
public class AuthzUserAllotRoleVo {

	/**
	 * 角色ID
	 */
	@ApiModelProperty(value = "roleId", required = true, dataType = "String", notes = "角色ID")
	private String roleId;
	/**
	 * 用户ID集合
	 */
	@ApiModelProperty(value = "userIds", required = true, dataType = "java.util.List<String>", notes = "用户ID集合")
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
