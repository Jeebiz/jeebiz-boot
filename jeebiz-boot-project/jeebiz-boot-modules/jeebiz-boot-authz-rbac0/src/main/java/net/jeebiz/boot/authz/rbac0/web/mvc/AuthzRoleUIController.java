/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.mvc;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.authz.rbac0.service.IAuthzRoleService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 权限管理：角色界面管理
 */
@Api(tags = "权限管理：角色界面管理（Ok）")
@Controller
@RequestMapping(value = "/authz/role/ui/")
public class AuthzRoleUIController extends BaseMapperController {

	@Autowired
	private IAuthzRoleService authzRoleService;//角色管理SERVICE
	
	@ApiIgnore
	@GetMapping("list")
	@RequiresPermissions("role:list")
	public String list() {
		return "html/authz/rbac0/role/list";
	}
	
	@ApiIgnore
	@GetMapping("new")
	@RequiresPermissions("role:new")
	public String newUser() {
		return "html/authz/rbac0/role/new";
	}
	
	@ApiIgnore
	@GetMapping("edit")
	@RequiresPermissions("role:edit")
	public String edit() {
		return "html/authz/rbac0/role/edit";
	}
	
	public IAuthzRoleService getAuthzRoleService() {
		return authzRoleService;
	}

	public void setAuthzRoleService(IAuthzRoleService authzRoleService) {
		this.authzRoleService = authzRoleService;
	}

}
