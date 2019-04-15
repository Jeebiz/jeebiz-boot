/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.service;


import java.util.List;

import net.jeebiz.boot.api.service.BaseService;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRolePermsModel;

/**
 * 
 * @author <a href="https://github.com/vindell">wandl</a>
 */
public interface IAuthzRolePermsService extends BaseService<AuthzRolePermsModel>{
    
	/**
	 * 查询角色具备的权限标记 
	 * @param roleId 角色ID
	 * @return 角色具备的权限标记
	 */
	public List<String> getPermissions(String roleId);
	
	/**
	 * 执行角色分配权限逻辑操作
	 * @param model
	 * @return
	 */
	public int doPerms(AuthzRolePermsModel model);
	
	/**
	 * 取消已分配给指定角色的权限
	 * @param model
	 * @return
	 */
	public int unPerms(AuthzRolePermsModel model);
	
}
