package net.jeebiz.boot.authz.feature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;

@Mapper
public interface IAuthzFeatureDao extends BaseDao<AuthzFeatureModel> {

	public List<AuthzFeatureModel> getFeatureList();
	
}
