/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.mvc;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureOptService;
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureService;
import net.jeebiz.boot.authz.feature.setup.handler.FeatureFlatDataHandler;
import net.jeebiz.boot.authz.feature.setup.handler.FeatureTreeDataHandler;
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
	@Autowired
	private IAuthzFeatureService authzFeatureService;
	@Autowired
	private IAuthzFeatureOptService authzFeatureOptService;
	@Autowired
	private FeatureTreeDataHandler featureTreeDataHandler;
	@Autowired
	private FeatureFlatDataHandler featureFlatDataHandler;
	
	@ApiIgnore
	@GetMapping("list")
	@RequiresPermissions("role:list")
	public String list() {
		return "html/authz/rbac0/role/list";
	}
	
	@ApiIgnore
	@GetMapping("new")
	@RequiresPermissions("role:new")
	public String newRole(Model model) {
		
		// 所有的功能菜单
		List<AuthzFeatureModel> featureList = getAuthzFeatureService().getFeatureList();
		// 所有的功能操作按钮
		List<AuthzFeatureOptModel> featureOptList = getAuthzFeatureOptService().getFeatureOpts();
		// 返回各级菜单 + 对应的功能权限数据
		model.addAttribute("features", getFeatureFlatDataHandler().handle(featureList, featureOptList));
		
		return "html/authz/rbac0/role/new";
	}
	
	@ApiIgnore
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleId", required = true, value = "角色信息ID", dataType = "String")
	})
	@GetMapping("renew")
	@RequiresPermissions("role:renew")
	public String renew(@RequestParam String roleId, Model model) {
		
		// 所有的功能菜单
		List<AuthzFeatureModel> featureList = getAuthzFeatureService().getFeatureList();
		// 所有的功能操作按钮
		List<AuthzFeatureOptModel> featureOptList = getAuthzRoleService().getFeatureOpts(roleId);
		// 返回各级菜单 + 对应的功能权限数据
		model.addAttribute("features", getFeatureFlatDataHandler().handle(featureList, featureOptList));
		
		return "html/authz/rbac0/role/renew";
	}

	public IAuthzRoleService getAuthzRoleService() {
		return authzRoleService;
	}

	public IAuthzFeatureService getAuthzFeatureService() {
		return authzFeatureService;
	}

	public IAuthzFeatureOptService getAuthzFeatureOptService() {
		return authzFeatureOptService;
	}

	public FeatureTreeDataHandler getFeatureTreeDataHandler() {
		return featureTreeDataHandler;
	}

	public FeatureFlatDataHandler getFeatureFlatDataHandler() {
		return featureFlatDataHandler;
	}

}
