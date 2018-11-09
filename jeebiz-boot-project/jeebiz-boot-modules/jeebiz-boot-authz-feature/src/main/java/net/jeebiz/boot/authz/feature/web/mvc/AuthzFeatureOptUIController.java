/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.feature.web.mvc;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureOptService;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "功能操作：界面跳转（Ok）")
@Controller
@RequestMapping(value = "/extras/feature/opt/ui/")
public class AuthzFeatureOptUIController extends BaseMapperController{

	@Autowired
	protected IAuthzFeatureOptService optService;
	
	@ApiIgnore
	@GetMapping("list")
	@RequiresPermissions("opt:list")
	public String list() {
		return "html/authz/opt/list";
	}

	@ApiIgnore
	@GetMapping("new")
	@RequiresPermissions("opt:new")
	public String newOpt() {
		return "html/authz/opt/new";
	}
	
	@ApiIgnore
	@GetMapping("edit")
	@RequiresPermissions("opt:edit")
	public String edit() {
		return "html/authz/opt/edit";
	}

	public IAuthzFeatureOptService getOptService() {
		return optService;
	}

	public void setOptService(IAuthzFeatureOptService optService) {
		this.optService = optService;
	}
	
}
