/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.feature.setup.handler;

import java.util.Map;

import com.google.common.collect.Maps;

public class FeatureDataHandlerFactory {

	private static Map<String, FeatureDataHandler> COMPLETED_HANDLER = Maps.newConcurrentMap();
	private static FeatureTreeDataHandler featureTreeDataHandler = new FeatureTreeDataHandler();
	private static FeatureFlatDataHandler featureFlatDataHandler = new FeatureFlatDataHandler();
	
	static {
		COMPLETED_HANDLER.put("tree", featureTreeDataHandler);
		COMPLETED_HANDLER.put("flat", featureFlatDataHandler);
	}
	
	public static FeatureDataHandler newHandler(String key, FeatureDataHandler handler) {
		FeatureDataHandler rt = COMPLETED_HANDLER.get(key);
		if(rt != null) {
			return rt;
		}
		return COMPLETED_HANDLER.putIfAbsent(key, handler);
	}
	
	public static FeatureDataHandler getTreeHandler(String key) {
		return COMPLETED_HANDLER.getOrDefault(key, featureTreeDataHandler);
	}
	
	public static FeatureDataHandler getFlatHandler(String key) {
		return COMPLETED_HANDLER.getOrDefault(key, featureFlatDataHandler);
	}
	
	
}
