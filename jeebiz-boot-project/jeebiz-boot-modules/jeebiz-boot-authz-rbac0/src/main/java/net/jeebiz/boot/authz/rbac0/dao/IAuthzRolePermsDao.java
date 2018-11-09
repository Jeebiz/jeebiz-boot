package net.jeebiz.boot.authz.rbac0.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRolePermsModel;

/**
 * 角色权限管理Dao
 * @author vindell
 */
@Mapper
public interface IAuthzRolePermsDao extends BaseDao<AuthzRolePermsModel>{
	
	/**
	 * 给角色分配功能权限
	 * @param roleId 角色ID
	 * @param perms 权限标记集合
	 * @return 变更记录数
	 */
	public int setPerms(@Param(value = "roleId") String roleId,@Param(value = "perms") List<String> perms);
	
	/**
	 * 删除角色功能权限
	 * @param roleId 角色ID
	 * @param perms 权限标记集合
	 * @return 变更记录数
	 */
	public int delPerms(@Param(value = "roleId") String roleId,@Param(value = "perms") List<String> perms);
	
	/**
	 * 根据[ROLE_PERMISSION_RELATION]数据查询角色具备的权限信息 
	 * @param roleId 角色ID
	 * @return 角色具备的权限信息
	 */
	public List<String> getPermissions(@Param(value="roleId")String roleId);
		
}
