/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.mvc;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.biz.authz.principal.ShiroPrincipal;
import org.apache.shiro.biz.utils.SubjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
	public String newUser(Model model) {
		model.addAttribute("roles", getAuthzRoleService().getRoles());
		return "html/authz/rbac0/user/new";
	}
	
	@ApiIgnore
	@GetMapping("import")
	@RequiresPermissions("user:import")
	public String importUser(Model model) {
		return "html/authz/rbac0/user/import";
	}
	
	@ApiIgnore
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", required = true, value = "用户信息ID", dataType = "String")
	})
	@GetMapping("renew/{id}")
	@RequiresPermissions("user:renew")
	public String renew(@PathVariable("id") String id, Model model) {
		model.addAttribute("model", getAuthzUserService().getModel(id));
		model.addAttribute("roles", getAuthzRoleService().getRoles());
		return "html/authz/rbac0/user/renew";
	}
	
	@ApiIgnore
	@GetMapping("info")
	@RequiresAuthentication
	public String info(HttpServletRequest request, Model model) {
		ShiroPrincipal principal = SubjectUtils.getPrincipal(ShiroPrincipal.class);
		model.addAttribute("model", getAuthzUserService().getModel(principal.getUserid()));
		model.addAttribute("roles", getAuthzRoleService().getRoles());
		return "html/authz/rbac0/user/info";
	}
	
	@ApiIgnore
	@GetMapping("password")
	@RequiresAuthentication
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
