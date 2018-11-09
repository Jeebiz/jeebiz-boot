package net.jeebiz.boot.authz.feature.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;

public class FeatureParentPredicate implements Predicate {

	protected String parent;

	public FeatureParentPredicate(String parent){
		this.parent = parent;
	}
	
	@Override
	public boolean evaluate(Object object) {
		AuthzFeatureModel feature = (AuthzFeatureModel) object;
		return StringUtils.equals(parent, feature.getId());
	}

}
