/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.feature.service;

import java.util.List;

import net.jeebiz.boot.api.service.BaseService;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;

public interface IAuthzFeatureOptService extends BaseService<AuthzFeatureOptModel>{

	public List<AuthzFeatureOptModel> getFeatureOpts();
	
	public List<AuthzFeatureOptModel> getFeatureOptList(String featureId, boolean visible);
	
}
