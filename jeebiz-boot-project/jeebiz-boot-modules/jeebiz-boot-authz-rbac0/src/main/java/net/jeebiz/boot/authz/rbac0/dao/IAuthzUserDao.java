package net.jeebiz.boot.authz.rbac0.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserModel;
/**
 * 用户管理DAO
 * @author vindell
 */
@Mapper
public interface IAuthzUserDao extends BaseDao<AuthzUserDetailModel>{
	
	/**
	 * 增加用户详情记录
	 * @param model
	 * @return
	 */
	public int insertDetail(AuthzUserDetailModel model);
	
	/**
	 * 删除用户详情记录
	 * @param id
	 * @return
	 */
	public int deleteDetail(String id);
	
	/**
	 * 删除用户角色记录
	 * @param id
	 * @return
	 */
	public int deleteRole(String id);
	
	/**
	 * 批量删除用户详情
	 * @param list
	 * @return
	 */
	public int batchDeleteDetail(@Param(value="list") List<?> list);
	
	/**
	 * 批量删除用户角色
	 * @param list
	 * @return
	 */
	public int batchDeleteRole(@Param(value="list") List<?> list);
	
	/**
	 * 更新用户详情记录
	 * @param model
	 * @return
	 */
	public int updateDetail(AuthzUserDetailModel model);
	
	/**
	 * 更新用户角色记录
	 * @param model
	 * @return
	 */
	public int updateRole(AuthzUserDetailModel model);
	
	/**
	 * 批量修改用户密码
	 * @param userid 用户ID
	 * @param password 新密码
	 * @return 变更记录数
	 */
	public int updatePwd(@Param(value="userId") String userId , @Param(value = "password") String password);

	/**
	 * 当前用户设置密码
	 * @param userId 用户ID
	 * @param oldPassword 旧密码
	 * @param password    新密码
	 * @return
	 */
	public int resetPwd(@Param(value="userId") String userId, @Param(value="oldPassword") String oldPassword,@Param(value="password") String password);

	/**
	 * 更新用户状态
	 * @param roleId 用户ID
	 * @param status 用户状态（0:禁用|1:可用|2:锁定）
	 * @return
	 */
	public int setStatus(@Param("userId") String userId, @Param("status") String status);
	
	/**
	 * 根据名称查询相同名称角用户数量
	 * @param username 用户名
	 * @return
	 */
	public int getCountByName( @Param("username") String username);
	
	/**
	 * 获取用户已分配角色ID
	 * @param userId 用户ID
	 * @return
	 */
	public List<String> getRoles(@Param(value="userId") String userId);
	
	/**
	 * 根据[ROLE_PERMISSION_RELATION]数据查询角色具备的权限信息 
	 * @param userId 用户ID
	 * @return 角色具备的权限信息
	 */
	public List<String> getPermissions(@Param(value="userId")String userId);

	/**
	 * 分页查询用户已分配角色信息
	 * @param page
	 * @param model
	 * @return
	 */
	public List<AuthzRoleModel> getPagedAllocatedList(Page<AuthzRoleModel> page, AuthzUserModel model);
	
	/**
	 * 分页查询用户未分配角色信息
	 * @param page
	 * @param model
	 * @return
	 */
	public List<AuthzRoleModel> getPagedUnAllocatedList(Page<AuthzRoleModel> page, AuthzUserModel model);
	
	
}
