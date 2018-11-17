/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.mvc;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.biz.authz.principal.ShiroPrincipal;
import org.apache.shiro.biz.utils.SubjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.authz.rbac0.service.IAuthzRoleService;
import net.jeebiz.boot.authz.rbac0.service.IAuthzUserService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 权限管理：用户界面管理
 */
@Api(tags = "权限管理：用户界面管理（Ok）")
@Controller
@RequestMapping(value = "/authz/user/ui")
public class AuthzUserUIController extends BaseMapperController {

	@Autowired
	private IAuthzRoleService authzRoleService;//角色管理SERVICE
	@Autowired
	private IAuthzUserService authzUserService;//用户管理SERVICE
	
	@ApiIgnore
	@GetMapping("list")
	@RequiresPermissions("user:list")
	public String list() {
		return "html/authz/rbac0/user/list";
	}
	
	@ApiIgnore
	@GetMapping("new")
	@RequiresPermissions("user:new")
	public String newUser() {
		return "html/authz/rbac0/user/new";
	}
	
	@ApiIgnore
	@GetMapping("edit")
	@RequiresPermissions("user:edit")
	public String edit() {
		return "html/authz/rbac0/user/edit";
	}
	
	@ApiIgnore
	@GetMapping("info")
	@RequiresPermissions("user:set-info")
	public String info(HttpServletRequest request, Model model) {
		ShiroPrincipal principal = SubjectUtils.getPrincipal(ShiroPrincipal.class);
		model.addAttribute("info", getAuthzUserService().getModel(principal.getUserid()));
		model.addAttribute("roles", getAuthzRoleService().getRoles());
		return "html/authz/rbac0/user/info";
	}
	
	@ApiIgnore
	@GetMapping("password")
	@RequiresPermissions("user:set-pwd")
	public String password() {
		return "html/authz/rbac0/user/password";
	}
	
	public IAuthzRoleService getAuthzRoleService() {
		return authzRoleService;
	}

	public void setAuthzRoleService(IAuthzRoleService authzRoleService) {
		this.authzRoleService = authzRoleService;
	}

	public IAuthzUserService getAuthzUserService() {
		return authzUserService;
	}

	public void setAuthzUserService(IAuthzUserService authzUserService) {
		this.authzUserService = authzUserService;
	}
	
}
