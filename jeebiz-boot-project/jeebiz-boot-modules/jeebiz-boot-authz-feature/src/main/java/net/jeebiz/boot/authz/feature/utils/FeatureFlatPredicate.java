/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.feature.utils;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;

import net.jeebiz.boot.authz.feature.dao.entities.AuthzFeatureModel;

public class FeatureFlatPredicate implements Predicate {

	protected String parent;

	public FeatureFlatPredicate(String parent){
		this.parent = parent;
	}
	
	@Override
	public boolean evaluate(Object object) {
		AuthzFeatureModel feature = (AuthzFeatureModel) object;
		return StringUtils.equals(parent, feature.getParent());
	}

}
