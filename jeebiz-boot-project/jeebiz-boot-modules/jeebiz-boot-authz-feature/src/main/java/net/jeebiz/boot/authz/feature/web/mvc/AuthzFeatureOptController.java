/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.feature.web.mvc;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.jeebiz.boot.api.annotation.BusinessLog;
import net.jeebiz.boot.api.annotation.BusinessType;
import net.jeebiz.boot.api.utils.Constants;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureOptService;
import net.jeebiz.boot.authz.feature.web.vo.AuthzFeatureOptVo;

@Api(tags = "功能操作：数据维护（Ok）")
@Controller
@RequestMapping(value = "/extras/feature/opt/")
public class AuthzFeatureOptController extends BaseMapperController{

	@Autowired
	protected IAuthzFeatureOptService authzFeatureOptService;
	
	@ApiOperation(value = "opt:new", notes = "增加功能操作代码信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "optVo", value = "功能操作代码信息", dataType = "ExtrasOptVo")
	})
	@BusinessLog(module = Constants.AUTHZ_FEATURE_OPT, business = "新增功能操作代码-名称：${name}", opt = BusinessType.INSERT)
	@PostMapping("newOpt")
	@RequiresPermissions("opt:new")
	@ResponseBody
	public Object newOpt(@Valid @RequestBody AuthzFeatureOptVo optVo) throws Exception { 
		AuthzFeatureOptModel model = getBeanMapper().map(optVo, AuthzFeatureOptModel.class);
		int total = getAuthzFeatureOptService().insert(model);
		return success("opt.new.success", total);
	}
	
	@ApiOperation(value = "opt:edit", notes = "修改功能操作代码信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "optVo", value = "功能操作代码信息", dataType = "ExtrasOptVo")
	})
	@BusinessLog(module = Constants.AUTHZ_FEATURE_OPT, business = "修改功能操作代码-名称：${name}", opt = BusinessType.UPDATE)
	@PostMapping("editOpt")
	@RequiresPermissions("opt:edit")
	@ResponseBody
	public Object editOpt(@Valid @RequestBody AuthzFeatureOptVo optVo) throws Exception { 
		AuthzFeatureOptModel model = getBeanMapper().map(optVo, AuthzFeatureOptModel.class);
		int total = getAuthzFeatureOptService().update(model);
		return success("opt.edit.success", total);
	}
	
	@ApiOperation(value = "opt:detail", notes = "根据功能操作代码ID查询功能操作代码信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam( name = "id", required = true, value = "功能操作代码ID", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_FEATURE_OPT, business = "查询功能操作代码-ID：${optid}", opt = BusinessType.SELECT)
	@PostMapping("detail/{id}")
	@RequiresPermissions("opt:detail")
	@ResponseBody
	public Object detail(@PathVariable("id") String id) throws Exception { 
		return getAuthzFeatureOptService().getModel(id);
	}
	
	@ApiOperation(value = "opt:del", notes = "删除功能操作代码信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam( name = "id", required = true, value = "功能操作代码ID", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_FEATURE_OPT, business = "删除功能操作代码-名称：${optid}", opt = BusinessType.DELETE)
	@PostMapping("delete/{id}")
	@RequiresPermissions("opt:del")
	@ResponseBody
	public Object delOpt(@PathVariable String id) throws Exception { 
		int total = getAuthzFeatureOptService().delete(id);
		return success("opt.del.success", total);
	}

	public IAuthzFeatureOptService getAuthzFeatureOptService() {
		return authzFeatureOptService;
	}

	public void setAuthzFeatureOptService(IAuthzFeatureOptService authzFeatureOptService) {
		this.authzFeatureOptService = authzFeatureOptService;
	}
	
}
