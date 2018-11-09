package net.jeebiz.boot.authz.feature.service;

import java.util.List;

import net.jeebiz.boot.api.service.BaseService;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;

public interface IAuthzFeatureService extends BaseService<AuthzFeatureModel>{

	public List<AuthzFeatureModel> getFeatureList();
	
}
