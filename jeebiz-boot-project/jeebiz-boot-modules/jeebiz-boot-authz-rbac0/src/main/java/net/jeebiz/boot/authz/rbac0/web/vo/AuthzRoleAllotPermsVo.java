package net.jeebiz.boot.authz.rbac0.web.vo;

import java.util.List;

import com.google.common.collect.Lists;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "AuthzRoleAllotPermsVo", description = "角色分配权限参数Vo")
public class AuthzRoleAllotPermsVo {
	
	/**
	 * 角色ID
	 */
	@ApiModelProperty(value = "roleId", dataType = "String", notes = "角色ID")
	private String roleId;
	/**
	 * 角色授权的标记集合
	 */
	@ApiModelProperty(value = "perms", required = true, dataType = "java.util.List<String>", notes = "角色授权的标记集合")
	private List<String> perms = Lists.newArrayList();

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public List<String> getPerms() {
		return perms;
	}

	public void setPerms(List<String> perms) {
		this.perms = perms;
	}

}
