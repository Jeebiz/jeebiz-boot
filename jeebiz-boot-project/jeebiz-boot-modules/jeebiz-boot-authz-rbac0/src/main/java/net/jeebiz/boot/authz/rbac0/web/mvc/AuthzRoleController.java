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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.jeebiz.boot.api.annotation.BusinessLog;
import net.jeebiz.boot.api.annotation.BusinessType;
import net.jeebiz.boot.api.utils.Constants;
import net.jeebiz.boot.api.utils.ResultUtils;
import net.jeebiz.boot.api.webmvc.BaseMapperController;
import net.jeebiz.boot.api.webmvc.Result;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureService;
import net.jeebiz.boot.authz.feature.setup.handler.FeatureFlatDataHandler;
import net.jeebiz.boot.authz.feature.setup.handler.FeatureTreeDataHandler;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleAllotUserModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;
import net.jeebiz.boot.authz.rbac0.service.IAuthzRoleService;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzRoleAllotUserVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzRolePaginationVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzRoleVo;
import net.jeebiz.boot.authz.rbac0.web.vo.AuthzUserDetailVo;

/**
 * 权限管理：角色管理
 */
@Api(tags = "权限管理：角色管理（Ok）")
@RestController
@RequestMapping(value = "/authz/role/")
public class AuthzRoleController extends BaseMapperController {

	@Autowired
	protected IAuthzFeatureService authzFeatureService;
	@Autowired
	private IAuthzRoleService authzRoleService;//角色管理SERVICE
	@Autowired
	protected FeatureTreeDataHandler featureTreeDataHandler;
	@Autowired
	protected FeatureFlatDataHandler featureFlatDataHandler;
	
	@ApiOperation(value = "role:roles", notes = "查询全部可用角色信息")
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "查询全部可用角色信息", opt = BusinessType.SELECT)
	@PostMapping("roles")
	@RequiresPermissions("role:list")
	@ResponseBody
	public Object roles(){
		return ResultUtils.dataMap(STATUS_SUCCESS, "角色信息列表", getAuthzRoleService().getRoles());
	}
	
	@ApiOperation(value = "role:list", notes = "分页查询角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "paginationVo", value = "角色信息筛选条件", dataType = "AuthzRolePaginationVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "分页查询角色信息", opt = BusinessType.SELECT)
	@PostMapping("list")
	@RequiresPermissions("role:list")
	@ResponseBody
	public Object list(@Valid @RequestBody AuthzRolePaginationVo paginationVo){
		
		AuthzRoleModel model = getBeanMapper().map(paginationVo, AuthzRoleModel.class);
		Page<AuthzRoleModel> pageResult = getAuthzRoleService().getPagedList(model);
		List<AuthzRoleVo> retList = new ArrayList<AuthzRoleVo>();
		for (AuthzRoleModel roleModel : pageResult.getRecords()) {
			retList.add(getBeanMapper().map(roleModel, AuthzRoleVo.class));
		}
		
		return new Result<AuthzRoleVo>(pageResult, retList);
	}
	
	@ApiOperation(value = "role:new", notes = "增加角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "roleVo", value = "角色信息", dataType = "AuthzRoleVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "新增角色-名称：${name}", opt = BusinessType.INSERT)
	@PostMapping("new")
	@RequiresPermissions("role:new")
	@ResponseBody
	public Object newRole(@Valid @RequestBody AuthzRoleVo roleVo) throws Exception { 
		AuthzRoleModel model = getBeanMapper().map(roleVo, AuthzRoleModel.class);
		int total = getAuthzRoleService().insert(model);
		return success("role.new.success", total);
	}
	
	@ApiOperation(value = "role:edit", notes = "修改角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "roleVo", value = "角色信息", dataType = "AuthzRoleVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "修改角色-名称：${name}", opt = BusinessType.UPDATE)
	@PostMapping("edit")
	@RequiresPermissions("role:edit")
	@ResponseBody
	public Object editRole(@Valid @RequestBody AuthzRoleVo roleVo) throws Exception { 
		AuthzRoleModel model = getBeanMapper().map(roleVo, AuthzRoleModel.class);
		int total = getAuthzRoleService().update(model);
		return success("role.edit.success", total);
	}
	
	@ApiOperation(value = "role:status", notes = "更新角色状态")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", required = true, value = "角色ID", dataType = "String"),
		@ApiImplicitParam(name = "status", required = true, value = "角色状态", dataType = "String", allowableValues = "2,1,0")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "更新角色状态", opt = BusinessType.UPDATE)
	@PostMapping(value = "status")
	@RequiresPermissions("role:status")
	@ResponseBody
	public Object status(@RequestParam String id, @RequestParam String status) throws Exception {
		int result = getAuthzRoleService().setStatus(id, status);
		if(result == 1) {
			return success("role.status.success", result);
		}
		return fail("role.status.fail", result);
	}
	
	@ApiOperation(value = "role:detail", notes = "根据角色ID查询角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam( name = "id", required = true, value = "角色ID", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "查询角色", opt = BusinessType.SELECT)
	@PostMapping("detail/{id}")
	@RequiresPermissions("role:detail")
	@ResponseBody
	public Object detail(@PathVariable("id") String id) throws Exception { 
		return getAuthzRoleService().getModel(id);
	}
	
	@ApiOperation(value = "role:del", notes = "删除角色信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam( name = "id", required = true, value = "角色ID", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "删除角色-名称：${roleid}", opt = BusinessType.DELETE)
	@PostMapping("delete/{id}")
	@RequiresPermissions("role:del")
	@ResponseBody
	public Object delRole(@PathVariable("id") String id) throws Exception { 
		int total = getAuthzRoleService().delete(id);
		return success("role.del.success", total);
	}
	
	@ApiOperation(value = "role:allocated", notes = "分页查询角色已分配用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "paginationVo", value = "已分配用户信息筛选条件", dataType = "AuthzRolePaginationVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "分页查询角色已分配用户信息,角色Id：${roleid}", opt = BusinessType.DELETE)
	@PostMapping("allocated")
	@RequiresPermissions("role:allocated")
	@ResponseBody
	public Object allocated(@Valid @RequestBody AuthzRolePaginationVo paginationVo){
		
		AuthzRoleModel model = getBeanMapper().map(paginationVo, AuthzRoleModel.class);
		Page<AuthzUserDetailModel> pageResult = getAuthzRoleService().getPagedAllocatedList(model);
		List<AuthzUserDetailVo> retList = new ArrayList<AuthzUserDetailVo>();
		for (AuthzUserDetailModel detailModel : pageResult.getRecords()) {
			retList.add(getBeanMapper().map(detailModel, AuthzUserDetailVo.class));
		}
		return new Result<AuthzUserDetailVo>(pageResult, retList);
	}
	
	@ApiOperation(value = "role:unallocated", notes = "分页查询角色未分配用户信息")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "paginationVo", value = "未分配用户信息筛选条件", dataType = "AuthzRolePaginationVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "分页查询角色未分配用户信息,角色Id：${roleid}", opt = BusinessType.DELETE)
	@PostMapping("unallocated")
	@RequiresPermissions("role:unallocated")
	@ResponseBody
	public Object unallocated(@Valid @RequestBody AuthzRolePaginationVo paginationVo){
		
		AuthzRoleModel model = getBeanMapper().map(paginationVo, AuthzRoleModel.class);
		Page<AuthzUserDetailModel> pageResult = getAuthzRoleService().getPagedUnAllocatedList(model);
		List<AuthzUserDetailVo> retList = new ArrayList<AuthzUserDetailVo>();
		for (AuthzUserDetailModel detailModel : pageResult.getRecords()) {
			retList.add(getBeanMapper().map(detailModel, AuthzUserDetailVo.class));
		}
		return new Result<AuthzUserDetailVo>(pageResult, retList);
	}
	
	@ApiOperation(value = "role:allot", notes = "给指定角色分配用户")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "allotVo", value = "角色分配的用户信息", dataType = "AuthzRoleAllotUserVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "给指定角色分配用户，角色Id：${roleid}", opt = BusinessType.DELETE)
	@PostMapping("allot")
	@RequiresPermissions("role:allot")
	@ResponseBody
	public Object allot(@Valid @RequestBody AuthzRoleAllotUserVo allotVo) throws Exception { 
		AuthzRoleAllotUserModel model = getBeanMapper().map(allotVo, AuthzRoleAllotUserModel.class);
		int total = getAuthzRoleService().doAllot(model);
		return success("role.allot.success", total); 
	}
	
	@ApiOperation(value = "role:unallot", notes = "取消已分配给指定角色的用户")
	@ApiImplicitParams({ 
		@ApiImplicitParam(paramType = "body", name = "allotVo", value = "角色取消分配的用户信息", dataType = "AuthzRoleAllotUserVo")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "取消已分配给指定角色的用户，角色Id：${roleid}", opt = BusinessType.DELETE)
	@PostMapping("unallot")
	@RequiresPermissions("role:unallot")
	@ResponseBody
	public Object unallot(@Valid @RequestBody AuthzRoleAllotUserVo allotVo) throws Exception { 
		AuthzRoleAllotUserModel model = getBeanMapper().map(allotVo, AuthzRoleAllotUserModel.class);
		int total = getAuthzRoleService().doUnAllot(model);
		return success("role.unallot.success", total); 
	}

	@ApiOperation(value = "role:features", notes = "查询已分配给当前角色的功能")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "roleId", required = false, value = "角色ID", dataType = "String")
	})
	@BusinessLog(module = Constants.AUTHZ_ROLE, business = "查询已分配给当前角色的功能", opt = BusinessType.SELECT)
	@PostMapping("features")
	@RequiresAuthentication
	@ResponseBody
	public Object features(@RequestParam String roleId) throws Exception { 
		return getAuthzRoleService().getFeatures(roleId);
	}
	
	@ApiOperation(value = "feature:tree", notes = "查询功能菜单树形结构数据")
	@BusinessLog(module = Constants.AUTHZ_FEATURE, business = "查询功能菜单树形结构数据", opt = BusinessType.SELECT)
	@PostMapping("tree-features")
	@RequiresPermissions("role:features")
	@ResponseBody
	public Object tree(@RequestParam String roleId){
		// 所有的功能菜单
		List<AuthzFeatureModel> featureList = getAuthzFeatureService().getFeatureList();
		// 所有的功能操作按钮
		List<AuthzFeatureOptModel> featureOptList = getAuthzRoleService().getFeatureOpts(roleId);
		// 返回各级菜单 + 对应的功能权限数据
		return ResultUtils.dataMap(STATUS_SUCCESS, getFeatureTreeDataHandler().handle(featureList, featureOptList));
	}
	
	@ApiOperation(value = "feature:flat", notes = "查询功能菜单树扁平构数据")
	@BusinessLog(module = Constants.AUTHZ_FEATURE, business = "查询功能菜单扁平结构数据", opt = BusinessType.SELECT)
	@PostMapping("flat-features")
	@RequiresPermissions("role:features")
	@ResponseBody
	public Object flat(@RequestParam String roleId){
		// 所有的功能菜单
		List<AuthzFeatureModel> featureList = getAuthzFeatureService().getFeatureList();
		// 所有的功能操作按钮：标记按钮选中状态
		List<AuthzFeatureOptModel> featureOptList = getAuthzRoleService().getFeatureOpts(roleId);
		// 返回叶子节点菜单 + 对应的功能权限数据
		return ResultUtils.dataMap(STATUS_SUCCESS, getFeatureFlatDataHandler().handle(featureList, featureOptList));
	}
	
	public IAuthzRoleService getAuthzRoleService() {
		return authzRoleService;
	}

	public void setAuthzRoleService(IAuthzRoleService authzRoleService) {
		this.authzRoleService = authzRoleService;
	}
	
	public IAuthzFeatureService getAuthzFeatureService() {
		return authzFeatureService;
	}

	public void setAuthzFeatureService(IAuthzFeatureService authzFeatureService) {
		this.authzFeatureService = authzFeatureService;
	}
	
	public FeatureTreeDataHandler getFeatureTreeDataHandler() {
		return featureTreeDataHandler;
	}

	public void setFeatureTreeDataHandler(FeatureTreeDataHandler featureTreeDataHandler) {
		this.featureTreeDataHandler = featureTreeDataHandler;
	}

	public FeatureFlatDataHandler getFeatureFlatDataHandler() {
		return featureFlatDataHandler;
	}

	public void setFeatureFlatDataHandler(FeatureFlatDataHandler featureFlatDataHandler) {
		this.featureFlatDataHandler = featureFlatDataHandler;
	}

}
