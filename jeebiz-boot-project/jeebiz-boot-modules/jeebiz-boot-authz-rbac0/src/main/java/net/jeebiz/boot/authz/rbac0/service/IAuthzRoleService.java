/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.service;


import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.jeebiz.boot.api.service.BaseService;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleAllotUserModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;

/**
 * @author vindell
 */
public interface IAuthzRoleService extends BaseService<AuthzRoleModel>{
    
	/**
	 * 执行分配用户逻辑操作
	 * @param model
	 * @return
	 */
	public int doAllot(AuthzRoleAllotUserModel model);
	
	/**
	 * 取消已分配给指定角色的用户
	 * @param model
	 * @return
	 */
	public int doUnAllot(AuthzRoleAllotUserModel model);
	
	/**
	 * 更新角色状态
	 * @param roleId 角色ID
	 * @param status 角色状态（0:禁用|1:可用）
	 * @return
	 */
	public int setStatus( String roleId, String status);

	/**
	 * 根据名称查询相同名称角色数量
	 * @return
	 */
	public int getCountByName(String roleName);
	
	/**
	 * 查询系统可用角色信息
	 * @return
	 */
	public List<AuthzRoleModel> getRoles();
	
	/**
	 * 查询指定角色ID拥有的功能菜单
	 * @param roleId
	 * @return
	 */
	public List<AuthzFeatureModel> getFeatures(String roleId);
	
	/**
	 * 查找功能操作并标记指定角色拥有权限的功能操作选中状态
	 * @param roleId
	 * @return
	 */
	public List<AuthzFeatureOptModel> getFeatureOpts(String roleId);
	
	/**
	 * 分页查询角色已分配用户信息
	 * @param model
	 * @return
	 */
	public Page<AuthzUserDetailModel> getPagedAllocatedList(AuthzRoleModel model);
	
	/**
	 * 分页查询角色未分配用户信息
	 * @param model
	 * @return
	 */
	public Page<AuthzUserDetailModel> getPagedUnAllocatedList(AuthzRoleModel model);
	
}
