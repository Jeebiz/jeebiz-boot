package net.jeebiz.boot.authz.rbac0.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;

/**
 * 角色管理Dao
 * @author vindell
 */
@Mapper
public interface IAuthzRoleDao extends BaseDao<AuthzRoleModel>{
		
    /**
     * 给角色分配用户
     * @param roleId 角色ID
	 * @param userIds 用户ID集合
	 * @return 变更记录数
     */
	public int setUsers(@Param(value = "roleId") String roleId , @Param(value = "userIds") List<String> userIds);
	
	/**
	 * 
	 * 删除角色已分配的用户
	 * @param roleId 角色ID
	 * @param userIds 用户ID集合
	 * @return 变更记录数
	 */
	public  int deleteUsers(@Param(value = "roleId") String roleId , @Param(value = "userIds") List<String> userIds);
	
	/**
	 * 更新角色状态
	 * @param roleId 角色ID
	 * @param status 角色状态（0:禁用|1:可用）
	 * @return
	 */
	public int setStatus(@Param("roleId") String roleId, @Param("status") String status);
	
	/**
	 * 查询系统可用角色信息
	 * @return
	 */
	public List<AuthzRoleModel> getRoles();
	

	public List<AuthzFeatureModel> getFeatures(@Param(value = "roleId") String roleId);
	
	/**
	 * 分页查询角色已分配用户信息
	 * @param page
	 * @param model
	 * @return
	 */
	public List<AuthzUserDetailModel> getPagedAllocatedList(Page<AuthzUserDetailModel> page, AuthzRoleModel model);
	
	/**
	 * 分页查询角色未分配用户信息
	 * @param page
	 * @param model
	 * @return
	 */
	public List<AuthzUserDetailModel> getPagedUnAllocatedList(Page<AuthzUserDetailModel> page, AuthzRoleModel model);
		
}
