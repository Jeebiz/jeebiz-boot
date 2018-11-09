package net.jeebiz.boot.authz.feature.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import net.jeebiz.boot.api.service.BaseServiceImpl;
import net.jeebiz.boot.authz.feature.dao.IAuthzFeatureDao;
import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;
import net.jeebiz.boot.authz.feature.service.IAuthzFeatureService;

@Service
public class AuthzFeatureServiceImpl extends BaseServiceImpl<AuthzFeatureModel, IAuthzFeatureDao> implements IAuthzFeatureService {
 
	@Override
	public List<AuthzFeatureModel> getFeatureList() {
		return getDao().getFeatureList();
	}

}
