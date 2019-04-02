package net.jeebiz.boot.authz.rbac0.web.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.jeebiz.boot.api.vo.AbstractPaginationVo;

@ApiModel(value = "AuthzUserPaginationVo", description = "用户信息分页查询参数Vo")
public class AuthzUserPaginationVo extends AbstractPaginationVo {
	
	/**
	 * 用户名
	 */
	@ApiModelProperty(value = "username", dataType = "String", notes = "用户名")
	private String username;
	/**
	 * 用户状态(0:不可用|1:正常|2:锁定)
	 */
	@ApiModelProperty(value = "status", dataType = "String", notes = "用户状态(0:不可用|1:正常|2:锁定)")
	private String status;
	/**
	 * 身份证号码
	 */
	@ApiModelProperty(value = "idcard", dataType = "String", notes = "身份证号码")
	private String idcard;
	/**
	 * 角色ID
	 */
	@ApiModelProperty(value = "roleId", dataType = "String", notes = "角色ID")
	private String roleId;
	/**
	 * 性别：（male：男，female：女）
	 */
	@ApiModelProperty(value = "gender", dataType = "String", notes = "性别：（male：男，female：女）")
	private String gender;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}
