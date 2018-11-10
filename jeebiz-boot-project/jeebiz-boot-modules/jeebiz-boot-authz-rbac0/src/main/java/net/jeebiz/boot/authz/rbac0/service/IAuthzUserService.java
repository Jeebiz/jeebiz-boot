package net.jeebiz.boot.authz.rbac0.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import net.jeebiz.boot.api.service.BaseService;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserAllotRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserModel;


/**
 * 用户管理Service接口
 * @author 		： vindell（001）
 */
public interface IAuthzUserService extends BaseService<AuthzUserDetailModel> {
	
	/**
	 * 批量修改用户密码
	 * @param users 用户名数组	
	 * @param password 新密码
	 * @return 变更记录数
	 */
	public int updatePwd(List<String> users ,String password);

	/**
	 * 当前用户设置密码
	 * @param userId 用户ID
	 * @param oldPassword 旧密码
	 * @param password    新密码
	 * @return
	 */
	public int resetPwd(String userId,String oldPassword,String password);
	
	/**
	 * 更新用户状态
	 * @param roleId 用户ID
	 * @param status 用户状态（0:禁用|1:可用|2:锁定）
	 * @return
	 */
	public int setStatus( String userId, String status);
	
	/**
	 * 执行用户分配角色逻辑操作
	 * @author 		： vindell（001）
	 * @param model
	 * @return
	 */
	public int doAllot(AuthzUserAllotRoleModel model);
	
	/**
	 * 取消已分配给指定用户的角色
	 * @author 		： vindell（001）
	 * @param model
	 * @return
	 */
	public int doUnAllot(AuthzUserAllotRoleModel model);
	
	/**
	 * 获取用户已分配角色ID
	 * @author 		： vindell（001）
	 * @param userId 用户ID
	 * @return
	 */
	public List<String> getRoles(String userId);
	
	/**
	 * 查询角色具备的权限标记 
	 * @param userId 用户ID
	 * @return 用户所属角色具备的权限标记
	 */
	public List<String> getPermissions(String userId);
	
	/**
	 * 分页查询用户已分配角色信息
	 * @author 		： vindell（001）
	 * @param model
	 * @return
	 */
	public Page<AuthzRoleModel> getPagedAllocatedList(AuthzUserModel model);
	
	/**
	 * 分页查询用户未分配角色信息
	 * @author 		： vindell（001）
	 * @param model
	 * @return
	 */
	public Page<AuthzRoleModel> getPagedUnAllocatedList(AuthzUserModel model);
	
	
}
