package net.jeebiz.boot.authz.rbac0.service.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import net.jeebiz.boot.api.dao.entities.PaginationModel;
import net.jeebiz.boot.api.service.BaseServiceImpl;
import net.jeebiz.boot.authz.feature.dao.IAuthzFeatureDao;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.utils.FeatureNavUtils;
import net.jeebiz.boot.authz.rbac0.dao.IAuthzRoleDao;
import net.jeebiz.boot.authz.rbac0.dao.IAuthzRolePermsDao;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleAllotUserModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzRoleModel;
import net.jeebiz.boot.authz.rbac0.dao.entities.AuthzUserDetailModel;
import net.jeebiz.boot.authz.rbac0.service.IAuthzRoleService;
import net.jeebiz.boot.authz.rbac0.utils.AuthzPermsUtils;

@Service
public class AuthzRoleServiceImpl extends BaseServiceImpl<AuthzRoleModel, IAuthzRoleDao>
		implements IAuthzRoleService {
	
	@Autowired
	private IAuthzRolePermsDao authzRolePermsDao;
	@Autowired
	private IAuthzFeatureDao authzFeatureDao;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int insert(AuthzRoleModel model) {
		int ct = getDao().insert(model);
		// 此次提交的授权标记
		List<String> perms = AuthzPermsUtils.distinct(model.getPerms());
		// 有授权
		if( !CollectionUtils.isEmpty(perms)) {
			// 执行授权
			getAuthzRolePermsDao().setPerms(model.getId(), perms);
		}
		return ct;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int update(AuthzRoleModel model) {
		int ct = getDao().update(model);
		// 查询已经授权标记
		List<String> oldperms = getAuthzRolePermsDao().getPermissions(model.getId());
		// 此次提交的授权标记
		List<String> perms = AuthzPermsUtils.distinct(model.getPerms());
		// 之前没有权限
		if(CollectionUtils.isEmpty(oldperms)) {
			// 执行授权
			getAuthzRolePermsDao().setPerms(model.getId(), perms);
		}
		// 之前有权限,这里需要筛选出新增的权限和取消的权限
		else {
			// 授权标记增量
			List<String> increments = AuthzPermsUtils.increment(perms, oldperms);
			if(!CollectionUtils.isEmpty(increments)) {
				getAuthzRolePermsDao().setPerms(model.getId(), increments);
			}
			// 授权标记减量
			List<String> decrements = AuthzPermsUtils.decrement(perms, oldperms);
			if(!CollectionUtils.isEmpty(decrements)) {
				getAuthzRolePermsDao().delPerms(model.getId(), decrements);
			}
		}
		return ct;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(String id) {
		int ct = getDao().delete(id);
		// 删除授权
		getAuthzRolePermsDao().delPerms(id, Lists.newArrayList());
		return ct;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int setStatus(String roleId, String status) {
		return getDao().setStatus(roleId, status);
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int doAllot(AuthzRoleAllotUserModel model) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int doUnAllot(AuthzRoleAllotUserModel model) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<AuthzRoleModel> getRoles(){
		return getDao().getRoles();
	}
	
	@Override
	public List<AuthzFeatureModel> getFeatures(String roleId) {
		// 所有的功能菜单
		List<AuthzFeatureModel> features = getAuthzFeatureDao().getFeatureList();
		return FeatureNavUtils.getFeatureMergedList(features, getDao().getFeatures(roleId));
	}
	
	@Override
	public Page<AuthzUserDetailModel> getPagedAllocatedList(AuthzRoleModel model) {
		
		PaginationModel tModel = (PaginationModel) model;
		
		Page<AuthzUserDetailModel> page = new Page<AuthzUserDetailModel>(tModel.getPageNo(), tModel.getLimit());
		if("asc".equalsIgnoreCase(tModel.getSortOrder())) {
			page.setAsc(tModel.getSortName());
		} else {
			page.setDesc(tModel.getSortName());
		}
		
		List<AuthzUserDetailModel> records = getDao().getPagedAllocatedList(page, model);
		page.setRecords(records);
		
		return page;
		
	}
	
	@Override
	public Page<AuthzUserDetailModel> getPagedUnAllocatedList(AuthzRoleModel model) {
		
		PaginationModel tModel = (PaginationModel) model;
		
		Page<AuthzUserDetailModel> page = new Page<AuthzUserDetailModel>(tModel.getPageNo(), tModel.getLimit());
		if("asc".equalsIgnoreCase(tModel.getSortOrder())) {
			page.setAsc(tModel.getSortName());
		} else {
			page.setDesc(tModel.getSortName());
		}
		
		List<AuthzUserDetailModel> records = getDao().getPagedUnAllocatedList(page, model);
		page.setRecords(records);
		
		return page;
		
	}
	
	public IAuthzRolePermsDao getAuthzRolePermsDao() {
		return authzRolePermsDao;
	}

	public void setAuthzRolePermsDao(IAuthzRolePermsDao authzRolePermsDao) {
		this.authzRolePermsDao = authzRolePermsDao;
	}

	public IAuthzFeatureDao getAuthzFeatureDao() {
		return authzFeatureDao;
	}

	public void setAuthzFeatureDao(IAuthzFeatureDao authzFeatureDao) {
		this.authzFeatureDao = authzFeatureDao;
	}

}
