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
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureService;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "功能菜单：界面跳转（Ok）")
@Controller
@RequestMapping(value = "/extras/feature/ui/")
public class AuthzFeatureUIController extends BaseMapperController{

	@Autowired
	protected IAuthzFeatureService featureService;
	
	@ApiIgnore
	@GetMapping("list")
	@RequiresPermissions("feature:list")
	public String list() {
		return "html/authz/feature/list";
	}
	
	@ApiIgnore
	@GetMapping("new")
	@RequiresPermissions("feature:new")
	public String newFeature() {
		return "html/authz/feature/new";
	}
	
	@ApiIgnore
	@GetMapping("edit")
	@RequiresPermissions("feature:edit")
	public String editFeature() {
		return "html/authz/feature/edit";
	}

	public IAuthzFeatureService getFeatureService() {
		return featureService;
	}

	public void setFeatureService(IAuthzFeatureService featureService) {
		this.featureService = featureService;
	}
	
}
