/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.mvc;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.biz.authz.principal.ShiroPrincipal;
import org.apache.shiro.biz.utils.SubjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.jeebiz.boot.api.annotation.BusinessLog;
import net.jeebiz.boot.api.annotation.BusinessType;
import net.jeebiz.boot.api.utils.Constants;
import net.jeebiz.boot.api.utils.StringUtils;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.api.webmvc.Result;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserAllotRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserModel;
import net.jeebiz.boot.authz.rbac0.service.IAuthzUserService;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzRoleVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzUserAllotRoleVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzUserDetailVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzUserPaginationVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzUserResetVo;

/**
 * 权限管理：用户管理
 */
@Api(tags = "权限管理：用户管理（Ok）")
@RestController
@RequestMapping(value = "/authz/user")
public class AuthzUserController extends BaseMapperController {

	@Autowired
	private IAuthzUserService authzUserService;//用户管理SERVICE
	
	@ApiOperation(value = "user:list", notes = "分页查询用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "paginationVo", value = "用户信息筛选条件", dataType = "AuthzUserPaginationVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "分页查询用户信息", opt = BusinessType.SELECT)
	@PostMapping("list")
	@RequiresPermissions("user:list")
	@ResponseBody
	public Object list(@Valid @RequestBody AuthzUserPaginationVo paginationVo){
		
		AuthzUserDetailModel model = getBeanMapper().map(paginationVo, AuthzUserDetailModel.class);
		Page<AuthzUserDetailModel> pageResult = getAuthzUserService().getPagedList(model);
		List<AuthzUserDetailVo> retList = new ArrayList<AuthzUserDetailVo>();
		for (AuthzUserDetailModel detailModel : pageResult.getRecords()) {
			retList.add(getBeanMapper().map(detailModel, AuthzUserDetailVo.class));
		}
		
		return new Result<AuthzUserDetailVo>(pageResult, retList);
	}
	
	@ApiOperation(value = "user:detail", notes = "根据用户ID查询用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam( name = "id", required = true, value = "用户ID", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "查询用户-ID：${userid}", opt = BusinessType.SELECT)
	@PostMapping("detail/{id}")
	@RequiresPermissions("user:detail")
	@ResponseBody
	public Object detail(@PathVariable String id) throws Exception { 
		return getAuthzUserService().getModel(id);
	}
	
	@ApiOperation(value = "user:current", notes = "根据认证信息中的用户ID查询用户详情")
	@BusinessLog(module = Constants.AUTHZ_USER, business = "根据认证信息中的用户ID查询用户详情", opt = BusinessType.SELECT)
	@PostMapping("detail")
	@RequiresAuthentication
	@ResponseBody
	public Object detail() throws Exception { 
		ShiroPrincipal principal = SubjectUtils.getPrincipal(ShiroPrincipal.class);
		return getAuthzUserService().getModel(principal.getUserid());
	}
	
	@ApiOperation(value = "user:new", notes = "增加用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "userVo", value = "用户信息", dataType = "AuthzUserDetailVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "新增用户-名称：${name}", opt = BusinessType.INSERT)
	@PostMapping("new")
	@RequiresPermissions("user:new")
	@ResponseBody
	public Object newUser(@Valid @RequestBody AuthzUserDetailVo userVo) throws Exception { 
		AuthzUserDetailModel model = getBeanMapper().map(userVo, AuthzUserDetailModel.class);
		int total = getAuthzUserService().insert(model);
		return success("user.new.success", total);
	}
	
	@ApiOperation(value = "user:renew", notes = "修改用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "userVo", value = "用户信息", dataType = "AuthzUserDetailVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "修改用户-名称：${name}", opt = BusinessType.UPDATE)
	@PostMapping("renew")
	@RequiresPermissions("user:renew")
	@ResponseBody
	public Object renewUser(@Valid @RequestBody AuthzUserDetailVo userVo) throws Exception { 
		AuthzUserDetailModel model = getBeanMapper().map(userVo, AuthzUserDetailModel.class);
		int total = getAuthzUserService().update(model);
		return success("user.renew.success", total);
	}
	
	@ApiOperation(value = "user:status", notes = "更新用户状态")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", required = true, value = "用户ID", dataType = "String"),
		@ApiImplicitParam(name = "status", required = true, value = "用户状态", dataType = "String", allowableValues = "1,0")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "更新用户状态", opt = BusinessType.UPDATE)
	@PostMapping("status")
	@RequiresPermissions("user:status")
	@ResponseBody
	public Object status(@RequestParam String id, @RequestParam String status) throws Exception {
		int result = getAuthzUserService().setStatus(id, status);
		if(result == 1) {
			return success("user.status.success", result);
		}
		return fail("user.status.fail", result);
	}
	
	@ApiOperation(value = "user:delete", notes = "删除用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "ids", value = "基础数据ID,多个用,拼接", required = true, dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "删除用户-名称：${userid}", opt = BusinessType.DELETE)
	@PostMapping("delete")
	@RequiresPermissions("user:delete")
	@ResponseBody
	public Object delRole(@RequestParam String ids) throws Exception {
		// 执行基础数据删除操作
		List<String> idList = Lists.newArrayList(StringUtils.tokenizeToStringArray(ids));
		int result = getAuthzUserService().batchDelete(idList);
		return success("user.del.success", result);
	}
	
	@ApiOperation(value = "user:allocated", notes = "分页查询用户已分配角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "paginationVo", value = "已分配角色信息筛选条件", dataType = "AuthzUserPaginationVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "分页查询用户已分配角色信息,用户Id：${userid}", opt = BusinessType.DELETE)
	@PostMapping("allocated")
	@RequiresPermissions("user:allocated")
	@ResponseBody
	public Object allocated(@Valid @RequestBody AuthzUserPaginationVo paginationVo){
		
		AuthzUserModel model = getBeanMapper().map(paginationVo, AuthzUserModel.class);
		Page<AuthzRoleModel> pageResult = getAuthzUserService().getPagedAllocatedList(model);
		List<AuthzRoleVo> retList = new ArrayList<AuthzRoleVo>();
		for (AuthzRoleModel userModel : pageResult.getRecords()) {
			retList.add(getBeanMapper().map(userModel, AuthzRoleVo.class));
		}
		return new Result<AuthzRoleVo>(pageResult, retList);
	}
	
	@ApiOperation(value = "user:unallocated", notes = "分页查询用户未分配角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "paginationVo", value = "未分配角色信息筛选条件", dataType = "AuthzUserPaginationVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "分页查询用户未分配角色信息,用户Id：${userid}", opt = BusinessType.DELETE)
	@PostMapping("unallocated")
	@RequiresPermissions("user:unallocated")
	@ResponseBody
	public Object unallocated(@Valid @RequestBody AuthzUserPaginationVo paginationVo){
		
		AuthzUserModel model = getBeanMapper().map(paginationVo, AuthzUserModel.class);
		Page<AuthzRoleModel> pageResult = getAuthzUserService().getPagedUnAllocatedList(model);
		List<AuthzRoleVo> retList = new ArrayList<AuthzRoleVo>();
		for (AuthzRoleModel userModel : pageResult.getRecords()) {
			retList.add(getBeanMapper().map(userModel, AuthzRoleVo.class));
		}
		return new Result<AuthzRoleVo>(pageResult, retList);
	}
	
	@ApiOperation(value = "user:allot", notes = "给指定用户分配角色")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "allotVo", value = "用户分配的角色信息", dataType = "AuthzUserAllotRoleVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "给指定用户分配角色，用户Id：${userid}", opt = BusinessType.DELETE)
	@PostMapping("allot")
	@RequiresPermissions("user:allot")
	@ResponseBody
	public Object allot(@Valid @RequestBody AuthzUserAllotRoleVo allotVo) throws Exception { 
		AuthzUserAllotRoleModel model = getBeanMapper().map(allotVo, AuthzUserAllotRoleModel.class);
		int total = getAuthzUserService().doAllot(model);
		return success("user.allot.success", total); 
	}
	
	@ApiOperation(value = "user:unallot", notes = "取消已分配给指定用户的角色")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "allotVo", value = "用户取消分配的角色信息", dataType = "AuthzUserAllotRoleVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "取消已分配给指定用户的角色，用户Id：${userid}", opt = BusinessType.DELETE)
	@PostMapping("unallot")
	@RequiresPermissions("user:unallot")
	@ResponseBody
	public Object unallot(@Valid @RequestBody AuthzUserAllotRoleVo allotVo) throws Exception { 
		AuthzUserAllotRoleModel model = getBeanMapper().map(allotVo, AuthzUserAllotRoleModel.class);
		int total = getAuthzUserService().doUnAllot(model);
		return success("user.unallot.success", total); 
	}
	
	/*------------个人功能-----------------------*/
	
	@ApiOperation(value = "user:reset-info", notes = "设置个人信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "resetVo", value = "用户信息", dataType = "AuthzUserResetVo")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "设置个人信息-名称：${username}", opt = BusinessType.UPDATE)
	@PostMapping("reset/info")
	@RequiresPermissions("user:reset-info")
	@ResponseBody
	public Object resetInfo(@Valid @RequestBody AuthzUserResetVo resetVo) throws Exception { 
		
		ShiroPrincipal principal = SubjectUtils.getPrincipal(ShiroPrincipal.class);
		AuthzUserDetailModel model = getBeanMapper().map(resetVo, AuthzUserDetailModel.class);
		model.setId(principal.getUserid());
		int total = getAuthzUserService().update(model);
		if(total > 0) {
			return success("user.reset.info.success", total); 
		}
		return fail("user.reset.info.fail", total);
	}
	
	@ApiOperation(value = "user:reset-pwd", notes = "设置密码")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "oldPassword", required = true, value = "当前密码", dataType = "String"),
		@ApiImplicitParam(name = "password", required = true, value = "新密码", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_USER, business = "设置密码", opt = BusinessType.UPDATE)
	@PostMapping("reset/pwd")
	@RequiresPermissions("user:reset-pwd")
	@ResponseBody
	public Object resetPwd(@RequestParam String oldPassword, @RequestParam String password) throws Exception {
		
		ShiroPrincipal principal = SubjectUtils.getPrincipal(ShiroPrincipal.class);
		int total = getAuthzUserService().resetPwd(principal.getUserid(), oldPassword, password);
		if(total > 0) {
			return success("user.reset.pwd.success", total); 
		}
		return fail("user.reset.pwd.fail", total);
	}
	
	@ApiOperation(value = "user:perms", notes = "查询已分配给当前用户所属角色的权限")
	@BusinessLog(module = Constants.AUTHZ_USER, business = "查询已分配给当前用户所属角色的权限", opt = BusinessType.SELECT)
	@PostMapping("perms")
	@RequiresPermissions("user:perms")
	@ResponseBody
	public Object perms() throws Exception { 
		String userId = SubjectUtils.getPrincipal(ShiroPrincipal.class).getUserid();
		return getAuthzUserService().getPermissions(userId);
	}
	
	public IAuthzUserService getAuthzUserService() {
		return authzUserService;
	}

	public void setAuthzUserService(IAuthzUserService authzUserService) {
		this.authzUserService = authzUserService;
	}
	
}
