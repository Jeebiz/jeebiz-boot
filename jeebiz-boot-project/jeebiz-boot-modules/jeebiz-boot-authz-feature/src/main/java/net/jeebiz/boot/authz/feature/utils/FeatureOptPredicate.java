package net.jeebiz.boot.authz.feature.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureOptModel;

public class FeatureOptPredicate implements Predicate {

	protected String id;

	public FeatureOptPredicate(String id){
		this.id = id;
	}
	
	@Override
	public boolean evaluate(Object object) {
		AuthzFeatureOptModel feature = (AuthzFeatureOptModel) object;
		return StringUtils.equals(id, feature.getFeatureId());
	}

}
