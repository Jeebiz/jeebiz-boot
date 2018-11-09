package net.jeebiz.boot.authz.feature.utils;

import org.apache.commons.collections.Predicate;

public class FeaturePredicateUtils extends org.apache.commons.collections.PredicateUtils {

	protected static FeatureLeafPredicate LEAF_PREDICATE = new FeatureLeafPredicate();
	
	public static Predicate childrenPredicate(String id) {
		return new FeatureChildrenPredicate(id);
	}
	
	public static Predicate parentPredicate(String parent) {
		return new FeatureParentPredicate(parent);
	}
	
	public static Predicate leafPredicate() {
		return LEAF_PREDICATE;
	}

}