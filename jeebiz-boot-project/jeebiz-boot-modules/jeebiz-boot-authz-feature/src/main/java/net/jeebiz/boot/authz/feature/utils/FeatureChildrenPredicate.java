package net.jeebiz.boot.authz.feature.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;

public class FeatureChildrenPredicate implements Predicate {

	protected String id;

	public FeatureChildrenPredicate(String id){
		this.id = id;
	}
	
	@Override
	public boolean evaluate(Object object) {
		AuthzFeatureModel feature = (AuthzFeatureModel) object;
		return StringUtils.equals(id, feature.getParent());
	}

}
