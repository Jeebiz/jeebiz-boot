/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.feature.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import net.jeebiz.boot.api.dao.BaseDao;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;

@Mapper
public interface IAuthzFeatureOptDao extends BaseDao<AuthzFeatureOptModel> {
	
	public List<AuthzFeatureOptModel> getFeatureOpts();
	
	public List<AuthzFeatureOptModel> getFeatureOptList(@Param(value = "featureId") String featureId, @Param(value = "visible") String visible);
	
}
