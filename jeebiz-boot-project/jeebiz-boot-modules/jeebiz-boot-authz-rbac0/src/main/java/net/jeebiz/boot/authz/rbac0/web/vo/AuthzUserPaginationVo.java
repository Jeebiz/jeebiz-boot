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

}
