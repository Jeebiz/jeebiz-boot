package net.jeebiz.boot.authz.feature.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;

public class FeatureLeafPredicate implements Predicate {

	@Override
	public boolean evaluate(Object object) {
		AuthzFeatureModel feature = (AuthzFeatureModel) object;
		return StringUtils.isNotEmpty(feature.getParent()) 
				&& !StringUtils.equals("0", feature.getParent())
				&& !StringUtils.equals("#", feature.getUrl());
	}

}
